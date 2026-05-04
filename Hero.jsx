import React from "react";
import { ArrowRight, Sparkles, FlaskConical } from "lucide-react";

const DOWNLOAD_URL = "https://studyshelf-download.netlify.app";

export const Hero = () => {
  return (
    <section
      id="top"
      className="relative overflow-hidden border-b border-white/5"
      data-testid="hero-section"
    >
      {/* Ambient glow */}
      <div
        aria-hidden
        className="pointer-events-none absolute -top-40 left-1/2 h-[520px] w-[520px] -translate-x-1/2 rounded-full bg-[#E07B39]/15 blur-[120px]"
      />
      <div
        aria-hidden
        className="pointer-events-none absolute inset-0 grain"
      />

      <div className="relative mx-auto max-w-6xl px-5 py-20 sm:px-8 sm:py-28 lg:py-36">
        <div className="flex flex-col items-start gap-8 sm:items-center sm:text-center">
          {/* Beta banner */}
          <div
            className="inline-flex items-center gap-2 rounded-full border border-[#E07B39]/25 bg-[#E07B39]/10 px-3.5 py-1.5 text-xs font-semibold text-[#E07B39] sm:text-sm"
            data-testid="beta-banner"
          >
            <FlaskConical className="h-3.5 w-3.5" />
            <span className="uppercase tracking-[0.18em]">
              Currently in Beta
            </span>
          </div>

          {/* Headline */}
          <h1
            className="font-display text-4xl font-semibold leading-[1.05] tracking-tight text-white sm:text-6xl lg:text-7xl"
            data-testid="hero-headline"
          >
            Study smarter,
            <br />
            <span className="text-[#E07B39]">not harder.</span>
          </h1>

          {/* Description */}
          <p
            className="max-w-xl text-base leading-relaxed text-white/60 sm:text-lg"
            data-testid="hero-description"
          >
            StudyShelf is a free, mobile-first learning platform for students.
            Browse study notes, ask the AI assistant, and prep for exams — all
            in one calm, distraction-light shelf.
          </p>

          {/* CTAs */}
          <div className="mt-2 flex w-full flex-col gap-3 sm:w-auto sm:flex-row sm:items-center">
            <a
              href={DOWNLOAD_URL}
              target="_blank"
              rel="noopener noreferrer"
              data-testid="hero-download-btn"
              className="group inline-flex items-center justify-center gap-2 rounded-full bg-[#E07B39] px-7 py-3.5 text-base font-medium text-white transition-all hover:bg-[#E07B39]"
            >
              <span>Download StudyShelf</span>
              <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
            </a>
            <a
              href="#features"
              data-testid="hero-learn-more-btn"
              className="inline-flex items-center justify-center gap-2 rounded-full border border-white/15 bg-transparent px-7 py-3.5 text-base font-medium text-white transition-all hover:border-white/40 hover:bg-white/5"
            >
              <Sparkles className="h-4 w-4 text-[#E07B39]" />
              <span>Learn More</span>
            </a>
          </div>

          {/* Trust line */}
          <p className="mt-4 text-xs text-white/40 sm:text-sm">
            Free forever · Mobile-first · Built for students 🇧🇩
          </p>
        </div>
      </div>
    </section>
  );
};

export default Hero;
