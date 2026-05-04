import React from "react";
import { Check, Lock } from "lucide-react";
import { useReveal } from "../hooks/useReveal";

const DOWNLOAD_URL = "https://studyshelf-download.netlify.app";

const FREE_PERKS = [
  "Full study notes library",
  "AI summaries, explanations & quizzes",
  "Exam prep & past papers",
  "Mobile-first, works on slow connections",
  "Light ads support development",
];

const PREMIUM_PERKS = [
  "Everything in Free",
  "Zero ads, zero distractions",
  "Faster AI responses",
  "Priority support",
  "Early access to new features",
];

export const Pricing = () => {
  const head = useReveal();
  const left = useReveal();
  const right = useReveal();

  return (
    <section
      id="pricing"
      className="relative border-b border-white/5 py-20 sm:py-28"
      data-testid="pricing-section"
    >
      <div className="mx-auto max-w-6xl px-5 sm:px-8">
        <div ref={head.ref} className={`${head.className} max-w-2xl`}>
          <p className="text-xs font-bold uppercase tracking-[0.22em] text-[#E07B39]">
            Pricing
          </p>
          <h2 className="mt-3 font-display text-3xl font-medium tracking-tight text-white sm:text-5xl">
            Free, always. Premium, when you’re ready.
          </h2>
          <p className="mt-4 text-base leading-relaxed text-white/55 sm:text-lg">
            Students shouldn’t have to pay to learn. Everything core is free —
            an ad-free Premium plan is coming for those who want it.
          </p>
        </div>

        <div className="mt-12 grid grid-cols-1 gap-5 lg:grid-cols-2">
          {/* Free plan */}
          <div
            ref={left.ref}
            className={`${left.className} relative rounded-2xl border border-white/10 bg-[#121212] p-7 sm:p-9`}
            data-testid="pricing-card-free"
          >
            <div className="flex items-center justify-between">
              <h3 className="font-display text-2xl font-medium text-white">
                Free
              </h3>
              <span className="rounded-full border border-white/10 bg-white/5 px-2.5 py-1 text-[10px] font-semibold uppercase tracking-wider text-white/70">
                Available now
              </span>
            </div>
            <div className="mt-6 flex items-baseline gap-2">
              <span className="font-display text-5xl font-semibold text-white">
                ৳0
              </span>
              <span className="text-sm text-white/50">/ forever</span>
            </div>
            <p className="mt-2 text-sm text-white/55">
              Ad-supported. Full access to notes, AI, and exam prep.
            </p>

            <ul className="mt-7 space-y-3">
              {FREE_PERKS.map((p) => (
                <li
                  key={p}
                  className="flex items-start gap-2.5 text-sm text-white/75"
                >
                  <Check className="mt-0.5 h-4 w-4 flex-shrink-0 text-[#E07B39]" />
                  <span>{p}</span>
                </li>
              ))}
            </ul>

            <a
              href={DOWNLOAD_URL}
              target="_blank"
              rel="noopener noreferrer"
              data-testid="pricing-free-cta"
              className="mt-8 inline-flex w-full items-center justify-center rounded-full bg-[#E07B39] px-6 py-3 text-sm font-medium text-white transition-colors hover:bg-[#E07B39]"
            >
              Download free
            </a>
          </div>

          {/* Premium plan */}
          <div
            ref={right.ref}
            className={`${right.className} relative overflow-hidden rounded-2xl border border-[#E07B39]/30 bg-[#121212] p-7 sm:p-9`}
            data-testid="pricing-card-premium"
          >
            <div
              aria-hidden
              className="pointer-events-none absolute -right-24 -top-24 h-64 w-64 rounded-full bg-[#E07B39]/15 blur-3xl"
            />
            <div className="relative flex items-center justify-between">
              <h3 className="font-display text-2xl font-medium text-white">
                Premium
              </h3>
              <span className="rounded-full bg-[#E07B39]/20 px-2.5 py-1 text-[10px] font-semibold uppercase tracking-wider text-[#E07B39]">
                Coming soon
              </span>
            </div>
            <div className="relative mt-6 flex items-baseline gap-2">
              <span className="font-display text-5xl font-semibold text-white/80">
                —
              </span>
              <span className="text-sm text-white/50">price TBA</span>
            </div>
            <p className="relative mt-2 text-sm text-white/55">
              Ad-free, faster, and built for serious study sessions.
            </p>

            <ul className="relative mt-7 space-y-3">
              {PREMIUM_PERKS.map((p) => (
                <li
                  key={p}
                  className="flex items-start gap-2.5 text-sm text-white/75"
                >
                  <Check className="mt-0.5 h-4 w-4 flex-shrink-0 text-[#E07B39]" />
                  <span>{p}</span>
                </li>
              ))}
            </ul>

            <a
              href="#waitlist"
              data-testid="pricing-premium-cta"
              className="relative mt-8 inline-flex w-full items-center justify-center gap-2 rounded-full border border-white/15 bg-white/5 px-6 py-3 text-sm font-medium text-white transition-colors hover:border-white/30 hover:bg-white/10"
            >
              <Lock className="h-3.5 w-3.5" />
              Join the waitlist
            </a>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Pricing;
