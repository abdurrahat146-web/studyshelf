from fastapi import FastAPI, APIRouter, HTTPException
from dotenv import load_dotenv
from starlette.middleware.cors import CORSMiddleware
from motor.motor_asyncio import AsyncIOMotorClient
import os
import logging
from pathlib import Path
from pydantic import BaseModel, Field, ConfigDict, EmailStr
from typing import List
import uuid
from datetime import datetime, timezone


ROOT_DIR = Path(__file__).parent
load_dotenv(ROOT_DIR / '.env')

# MongoDB connection
mongo_url = os.environ['MONGO_URL']
client = AsyncIOMotorClient(mongo_url)
db = client[os.environ['DB_NAME']]

app = FastAPI(title="StudyShelf API")
api_router = APIRouter(prefix="/api")


# ---------- Models ----------
class WaitlistCreate(BaseModel):
    email: EmailStr


class WaitlistEntry(BaseModel):
    model_config = ConfigDict(extra="ignore")
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    email: EmailStr
    created_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))


class ContactCreate(BaseModel):
    name: str = Field(min_length=1, max_length=100)
    email: EmailStr
    message: str = Field(min_length=1, max_length=5000)


class ContactMessage(BaseModel):
    model_config = ConfigDict(extra="ignore")
    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
    name: str
    email: EmailStr
    message: str
    created_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))


# ---------- Routes ----------
@api_router.get("/")
async def root():
    return {"message": "StudyShelf API is running"}


@api_router.post("/waitlist", response_model=WaitlistEntry, status_code=201)
async def join_waitlist(payload: WaitlistCreate):
    email = payload.email.lower().strip()
    existing = await db.waitlist.find_one({"email": email}, {"_id": 0})
    if existing:
        raise HTTPException(status_code=409, detail="This email is already on the waitlist.")
    entry = WaitlistEntry(email=email)
    doc = entry.model_dump()
    doc['created_at'] = doc['created_at'].isoformat()
    await db.waitlist.insert_one(doc)
    return entry


@api_router.get("/waitlist", response_model=List[WaitlistEntry])
async def list_waitlist():
    entries = await db.waitlist.find({}, {"_id": 0}).sort("created_at", -1).to_list(1000)
    for e in entries:
        if isinstance(e.get('created_at'), str):
            e['created_at'] = datetime.fromisoformat(e['created_at'])
    return entries


@api_router.post("/contact", response_model=ContactMessage, status_code=201)
async def submit_contact(payload: ContactCreate):
    msg = ContactMessage(
        name=payload.name.strip(),
        email=payload.email.lower().strip(),
        message=payload.message.strip(),
    )
    doc = msg.model_dump()
    doc['created_at'] = doc['created_at'].isoformat()
    await db.contact_messages.insert_one(doc)
    return msg


@api_router.get("/contact", response_model=List[ContactMessage])
async def list_contact():
    entries = await db.contact_messages.find({}, {"_id": 0}).sort("created_at", -1).to_list(1000)
    for e in entries:
        if isinstance(e.get('created_at'), str):
            e['created_at'] = datetime.fromisoformat(e['created_at'])
    return entries


app.include_router(api_router)

app.add_middleware(
    CORSMiddleware,
    allow_credentials=True,
    allow_origins=os.environ.get('CORS_ORIGINS', '*').split(','),
    allow_methods=["*"],
    allow_headers=["*"],
)

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


@app.on_event("startup")
async def ensure_indexes():
    await db.waitlist.create_index("email", unique=True)


@app.on_event("shutdown")
async def shutdown_db_client():
    client.close()
