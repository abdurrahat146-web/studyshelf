import React, { useState } from "react";
import { Mail, ArrowRight, CheckCircle2 } from "lucide-react";
import { toast } from "sonner";
import { api } from "../lib/api";
import { useReveal } from "../hooks/useReveal";

export const Waitlist = () => {
  const reveal = useReveal();
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const [done, setDone] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email.trim()) return;
    setLoading(true);
    try {
      await api.post("/waitlist", { email: email.trim() });
      setDone(true);
      toast.success("You're on the list! We'll email you when Premium drops.");
      setEmail("");
    } catch (err) {
      const status = err?.response?.status;
      const msg =
        err?.response?.data?.detail ||
        (status === 409
          ? "You're already on the waitlist."
          : "Something went wrong. Try again.");
      toast.error(typeof msg === "string" ? msg : "Something went wrong.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <section
      id="waitlist"
      className="relative border-b border-white/5 py-20 sm:py-28"
      data-testid="waitlist-section"
    >
      <div className="mx-auto max-w-3xl px-5 sm:px-8">
        <div
          ref={reveal.ref}
          className={`${reveal.className} relative overflow-hidden rounded-3xl border border-white/10 bg-[#121212] p-8 sm:p-12`}
        >
          <div
            aria-hidden
            className="pointer-events-none absolute -top-32 left-1/2 h-72 w-72 -translate-x-1/2 rounded-full bg-[#E07B39]/15 blur-3xl"
          />
          <div className="relative">
            <p className="text-xs font-bold uppercase tracking-[0.22em] text-[#E07B39]">
              Premium waitlist
            </p>
            <h2 className="mt-3 font-display text-3xl font-medium tracking-tight text-white sm:text-4xl">
              Be the first to go ad-free.
            </h2>
            <p className="mt-3 max-w-xl text-sm leading-relaxed text-white/55 sm:text-base">
              Drop your email and we’ll quietly notify you when Premium
              launches. No spam, no nonsense.
            </p>

            <form
              onSubmit={handleSubmit}
              className="mt-7 flex flex-col gap-3 sm:flex-row"
              data-testid="waitlist-form"
            >
              <div className="relative flex-1">
                <Mail className="pointer-events-none absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-white/40" />
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="you@school.edu"
                  data-testid="waitlist-email-input"
                  className="w-full rounded-full border border-white/10 bg-[#0a0a0a] py-3.5 pl-11 pr-4 text-sm text-white placeholder-white/30 outline-none transition-all focus:border-[#E07B39] focus:ring-2 focus:ring-[#E07B39]/30"
                />
              </div>
              <button
                type="submit"
                disabled={loading}
                data-testid="waitlist-submit-btn"
                className="group inline-flex items-center justify-center gap-2 rounded-full bg-[#E07B39] px-6 py-3.5 text-sm font-medium text-white transition-colors hover:bg-[#E07B39] disabled:cursor-not-allowed disabled:opacity-60"
              >
                {done ? (
                  <>
                    <CheckCircle2 className="h-4 w-4" /> Joined
                  </>
                ) : loading ? (
                  "Joining…"
                ) : (
                  <>
                    Join waitlist
                    <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-0.5" />
                  </>
                )}
              </button>
            </form>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Waitlist;
