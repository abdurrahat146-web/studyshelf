import React from "react";
import Logo from "./Logo";
import { ArrowRight } from "lucide-react";

const DOWNLOAD_URL = "https://studyshelf-download.netlify.app";

const NAV = [
  { label: "Features", href: "#features" },
  { label: "Pricing", href: "#pricing" },
  { label: "Contact", href: "#contact" },
];

export const Header = () => {
  return (
    <header
      className="sticky top-0 z-50 w-full border-b border-white/5 bg-[#0a0a0a]/80 backdrop-blur-xl"
      data-testid="site-header"
    >
      <div className="mx-auto flex h-16 max-w-6xl items-center justify-between px-5 sm:h-20 sm:px-8">
        <Logo />

        <nav className="hidden items-center gap-8 md:flex">
          {NAV.map((n) => (
            <a
              key={n.href}
              href={n.href}
              className="text-sm font-medium text-white/70 transition-colors hover:text-white"
              data-testid={`nav-link-${n.label.toLowerCase()}`}
            >
              {n.label}
            </a>
          ))}
        </nav>

        <a
          href={DOWNLOAD_URL}
          target="_blank"
          rel="noopener noreferrer"
          data-testid="header-download-btn"
          className="group inline-flex items-center gap-1.5 rounded-full bg-[#E07B39] px-4 py-2 text-sm font-medium text-white transition-all hover:bg-[#E07B39] sm:px-5 sm:py-2.5"
        >
          <span>Download</span>
          <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-0.5" />
        </a>
      </div>
    </header>
  );
};

export default Header;
