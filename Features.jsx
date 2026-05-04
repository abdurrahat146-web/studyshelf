import React from "react";
import {
  BookMarked,
  Sparkles,
  GraduationCap,
  Megaphone,
  Crown,
} from "lucide-react";
import { useReveal } from "../hooks/useReveal";

const FEATURES = [
  {
    icon: BookMarked,
    title: "Study Notes Library",
    desc: "Curated notes for school and college subjects, organised by class and chapter — searchable and saveable for offline review.",
    span: "lg:col-span-2",
    available: true,
  },
  {
    icon: Sparkles,
    title: "AI Study Assistant",
    desc: "Summarise long chapters, get plain-English explanations, and generate practice quizzes from any topic.",
    span: "lg:col-span-1",
    available: true,
  },
  {
    icon: GraduationCap,
    title: "Exam Preparation",
    desc: "Past papers, model tests, and topic-wise drills designed to make exam day feel familiar.",
    span: "lg:col-span-1",
    available: true,
  },
  {
    icon: Megaphone,
    title: "Free Access · Ad Supported",
    desc: "Everything core is free. Light ads keep the lights on so students never have to pay to learn.",
    span: "lg:col-span-2",
    available: true,
  },
  {
    icon: Crown,
    title: "Premium Ad-Free",
    desc: "An ad-free, faster experience for power-users. Coming soon — join the waitlist below.",
    span: "lg:col-span-3",
    available: false,
  },
];

const FeatureCard = ({ icon: Icon, title, desc, span, available }) => {
  const reveal = useReveal();
  return (
    <div
      ref={reveal.ref}
      data-testid={`feature-card-${title.toLowerCase().replace(/[^a-z0-9]+/g, "-")}`}
      className={`${reveal.className} ${span} group relative overflow-hidden rounded-2xl border border-white/10 bg-[#121212] p-7 transition-colors duration-300 hover:border-[#E07B39]/40 sm:p-8`}
    >
      <div className="mb-6 inline-flex h-11 w-11 items-center justify-center rounded-xl bg-[#E07B39]/10 ring-1 ring-[#E07B39]/20">
        <Icon className="h-5 w-5 text-[#E07B39]" strokeWidth={2.2} />
      </div>
      <div className="flex items-center gap-2">
        <h3 className="font-display text-xl font-medium text-white sm:text-2xl">
          {title}
        </h3>
        {!available && (
          <span className="rounded-full border border-white/10 bg-white/5 px-2 py-0.5 text-[10px] font-semibold uppercase tracking-wider text-white/60">
            Soon
          </span>
        )}
      </div>
      <p className="mt-3 text-sm leading-relaxed text-white/55 sm:text-base">
        {desc}
      </p>

      {!available && (
        <div
          aria-hidden
          className="pointer-events-none absolute -bottom-20 -right-20 h-56 w-56 rounded-full bg-[#E07B39]/15 blur-3xl"
        />
      )}
    </div>
  );
};

export const Features = () => {
  const head = useReveal();
  return (
    <section
      id="features"
      className="relative border-b border-white/5 py-20 sm:py-28"
      data-testid="features-section"
    >
      <div className="mx-auto max-w-6xl px-5 sm:px-8">
        <div ref={head.ref} className={head.className}>
          <p className="text-xs font-bold uppercase tracking-[0.22em] text-[#E07B39]">
            What you get
          </p>
          <h2 className="mt-3 max-w-2xl font-display text-3xl font-medium tracking-tight text-white sm:text-5xl">
            A calm shelf of everything <br className="hidden sm:block" />
            you actually need.
          </h2>
        </div>

        <div className="mt-12 grid grid-cols-1 gap-4 sm:gap-5 lg:grid-cols-3">
          {FEATURES.map((f) => (
            <FeatureCard key={f.title} {...f} />
          ))}
        </div>
      </div>
    </section>
  );
};

export default Features;
