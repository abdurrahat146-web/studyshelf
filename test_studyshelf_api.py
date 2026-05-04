"""StudyShelf backend API tests."""
import os
import uuid
import requests
import pytest

BASE_URL = os.environ.get("REACT_APP_BACKEND_URL", "https://shelf-learn.preview.emergentagent.com").rstrip("/")
API = f"{BASE_URL}/api"


@pytest.fixture(scope="session")
def unique_email():
    return f"test_{uuid.uuid4().hex[:10]}@example.com"


# Health
def test_root_running():
    r = requests.get(f"{API}/")
    assert r.status_code == 200
    assert "running" in r.json()["message"].lower()


# Waitlist
class TestWaitlist:
    def test_create_valid(self, unique_email):
        r = requests.post(f"{API}/waitlist", json={"email": unique_email})
        assert r.status_code == 201, r.text
        data = r.json()
        assert data["email"] == unique_email
        assert "id" in data and isinstance(data["id"], str)
        assert "_id" not in data

    def test_duplicate_returns_409(self, unique_email):
        r = requests.post(f"{API}/waitlist", json={"email": unique_email})
        assert r.status_code == 409

    def test_invalid_email_422(self):
        r = requests.post(f"{API}/waitlist", json={"email": "not-an-email"})
        assert r.status_code == 422

    def test_list_no_id_leak(self, unique_email):
        r = requests.get(f"{API}/waitlist")
        assert r.status_code == 200
        entries = r.json()
        assert isinstance(entries, list)
        assert any(e["email"] == unique_email for e in entries)
        for e in entries:
            assert "_id" not in e
            assert "id" in e and "email" in e


# Contact
class TestContact:
    def test_create_valid(self):
        payload = {
            "name": "TEST_User",
            "email": f"test_{uuid.uuid4().hex[:8]}@example.com",
            "message": "Hello, this is a test message.",
        }
        r = requests.post(f"{API}/contact", json=payload)
        assert r.status_code == 201, r.text
        data = r.json()
        assert data["name"] == payload["name"]
        assert data["email"] == payload["email"]
        assert data["message"] == payload["message"]
        assert "id" in data
        assert "_id" not in data

    def test_missing_fields_422(self):
        r = requests.post(f"{API}/contact", json={"name": "x"})
        assert r.status_code == 422

    def test_invalid_email_422(self):
        r = requests.post(
            f"{API}/contact",
            json={"name": "x", "email": "nope", "message": "hi"},
        )
        assert r.status_code == 422

    def test_empty_message_422(self):
        r = requests.post(
            f"{API}/contact",
            json={"name": "x", "email": "a@b.co", "message": ""},
        )
        assert r.status_code == 422

    def test_list_no_id_leak(self):
        r = requests.get(f"{API}/contact")
        assert r.status_code == 200
        entries = r.json()
        assert isinstance(entries, list)
        for e in entries:
            assert "_id" not in e
