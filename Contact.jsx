import React, { useState } from "react";
import { Mail, Send } from "lucide-react";
import { toast } from "sonner";
import { api } from "../lib/api";
import { useReveal } from "../hooks/useReveal";

const CONTACT_EMAIL = "abdurrahat146@gmail.com";

export const Contact = () => {
  const left = useReveal();
  const right = useReveal();
  const [form, setForm] = useState({ name: "", email: "", message: "" });
  const [loading, setLoading] = useState(false);

  const update = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name.trim() || !form.email.trim() || !form.message.trim()) {
      toast.error("Please fill in every field.");
      return;
    }
    setLoading(true);
    try {
      await api.post("/contact", form);
      toast.success("Message sent. We'll get back to you soon!");
      setForm({ name: "", email: "", message: "" });
    } catch (err) {
      const msg = err?.response?.data?.detail || "Couldn't send. Try again.";
      toast.error(typeof msg === "string" ? msg : "Couldn't send.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <section
      id="contact"
      className="relative py-20 sm:py-28"
      data-testid="contact-section"
    >
      <div className="mx-auto max-w-6xl px-5 sm:px-8">
        <div className="grid grid-cols-1 gap-10 lg:grid-cols-2 lg:gap-16">
          {/* Left: copy */}
          <div ref={left.ref} className={left.className}>
            <p className="text-xs font-bold uppercase tracking-[0.22em] text-[#E07B39]">
              Contact
            </p>
            <h2 className="mt-3 font-display text-3xl font-medium tracking-tight text-white sm:text-5xl">
              Got a question? We’re listening.
            </h2>
            <p className="mt-4 max-w-md text-base leading-relaxed text-white/55">
              Bug reports, feature ideas, or just want to say hi — drop a note.
              We read everything.
            </p>

            <div className="mt-8 inline-flex items-center gap-3 rounded-full border border-white/10 bg-[#121212] px-4 py-3">
              <span className="inline-flex h-8 w-8 items-center justify-center rounded-full bg-[#E07B39]/15">
                <Mail className="h-4 w-4 text-[#E07B39]" />
              </span>
              <a
                href={`mailto:${CONTACT_EMAIL}`}
                data-testid="contact-email-link"
                className="text-sm font-medium text-white hover:text-[#E07B39]"
              >
                {CONTACT_EMAIL}
              </a>
            </div>
          </div>

          {/* Right: form */}
          <form
            ref={right.ref}
            onSubmit={handleSubmit}
            className={`${right.className} rounded-3xl border border-white/10 bg-[#121212] p-7 sm:p-9`}
            data-testid="contact-form"
          >
            <div className="space-y-4">
              <div>
                <label
                  htmlFor="contact-name"
                  className="mb-2 block text-xs font-semibold uppercase tracking-wider text-white/60"
                >
                  Name
                </label>
                <input
                  id="contact-name"
                  type="text"
                  value={form.name}
                  onChange={update("name")}
                  placeholder="Your name"
                  required
                  data-testid="contact-name-input"
                  className="w-full rounded-xl border border-white/10 bg-[#0a0a0a] px-4 py-3 text-sm text-white placeholder-white/30 outline-none transition-all focus:border-[#E07B39] focus:ring-2 focus:ring-[#E07B39]/30"
                />
              </div>
              <div>
                <label
                  htmlFor="contact-email"
                  className="mb-2 block text-xs font-semibold uppercase tracking-wider text-white/60"
                >
                  Email
                </label>
                <input
                  id="contact-email"
                  type="email"
                  value={form.email}
                  onChange={update("email")}
                  placeholder="you@school.edu"
                  required
                  data-testid="contact-email-input"
                  className="w-full rounded-xl border border-white/10 bg-[#0a0a0a] px-4 py-3 text-sm text-white placeholder-white/30 outline-none transition-all focus:border-[#E07B39] focus:ring-2 focus:ring-[#E07B39]/30"
                />
              </div>
              <div>
                <label
                  htmlFor="contact-message"
                  className="mb-2 block text-xs font-semibold uppercase tracking-wider text-white/60"
                >
                  Message
                </label>
                <textarea
                  id="contact-message"
                  value={form.message}
                  onChange={update("message")}
                  placeholder="What's on your mind?"
                  required
                  rows={5}
                  data-testid="contact-message-input"
                  className="w-full resize-none rounded-xl border border-white/10 bg-[#0a0a0a] px-4 py-3 text-sm text-white placeholder-white/30 outline-none transition-all focus:border-[#E07B39] focus:ring-2 focus:ring-[#E07B39]/30"
                />
              </div>

              <button
                type="submit"
                disabled={loading}
                data-testid="contact-submit-btn"
                className="group mt-2 inline-flex w-full items-center justify-center gap-2 rounded-full bg-[#E07B39] px-6 py-3.5 text-sm font-medium text-white transition-colors hover:bg-[#E07B39] disabled:cursor-not-allowed disabled:opacity-60"
              >
                {loading ? "Sending…" : "Send message"}
                <Send className="h-4 w-4 transition-transform group-hover:translate-x-0.5" />
              </button>
            </div>
          </form>
        </div>
      </div>
    </section>
  );
};

export default Contact;
