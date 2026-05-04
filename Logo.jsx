import React from "react";

export const Logo = ({ className = "" }) => {
  return (
    <a
      href="#top"
      className={`group inline-flex items-center gap-2.5 ${className}`}
      data-testid="brand-logo-link"
    >
      <img
        src="/studyshelf-logo.svg"
        alt="StudyShelf logo"
        className="h-9 w-9 rounded-xl transition-transform duration-300 group-hover:rotate-[-4deg]"
        width="36"
        height="36"
      />
      <span className="font-display text-xl font-semibold tracking-tight text-white">
        Study<span className="text-[#E07B39]">Shelf</span>
      </span>
    </a>
  );
};

export default Logo;
