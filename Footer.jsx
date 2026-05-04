import React from "react";
import Logo from "./Logo";
import { Mail } from "lucide-react";

const CONTACT_EMAIL = "abdurrahat146@gmail.com";

export const Footer = () => {
  return (
    <footer
      className="relative border-t border-white/5 bg-[#070707] py-14"
      data-testid="site-footer"
    >
      <div className="mx-auto max-w-6xl px-5 sm:px-8">
        <div className="grid grid-cols-1 gap-10 sm:grid-cols-3">
          <div className="sm:col-span-1">
            <Logo />
            <p className="mt-4 max-w-xs text-sm leading-relaxed text-white/50">
              A free, mobile-first study companion built for high-school and
              college students.
            </p>
          </div>

          <div>
            <h4 className="text-xs font-bold uppercase tracking-[0.22em] text-white/70">
              Product
            </h4>
            <ul className="mt-4 space-y-2.5 text-sm text-white/55">
              <li>
                <a
                  href="#features"
                  className="hover:text-white"
                  data-testid="footer-link-features"
                >
                  Features
                </a>
              </li>
              <li>
                <a
                  href="#pricing"
                  className="hover:text-white"
                  data-testid="footer-link-pricing"
                >
                  Pricing
                </a>
              </li>
              <li>
                <a
                  href="#waitlist"
                  className="hover:text-white"
                  data-testid="footer-link-waitlist"
                >
                  Premium waitlist
                </a>
              </li>
            </ul>
          </div>

          <div>
            <h4 className="text-xs font-bold uppercase tracking-[0.22em] text-white/70">
              Contact
            </h4>
            <ul className="mt-4 space-y-2.5 text-sm text-white/55">
              <li>
                <a
                  href={`mailto:${CONTACT_EMAIL}`}
                  data-testid="footer-email-link"
                  className="inline-flex items-center gap-2 hover:text-white"
                >
                  <Mail className="h-3.5 w-3.5" />
                  {CONTACT_EMAIL}
                </a>
              </li>
              <li className="text-white/40">Currently in Beta</li>
            </ul>
          </div>
        </div>

        <div className="mt-12 flex flex-col items-start justify-between gap-3 border-t border-white/5 pt-6 sm:flex-row sm:items-center">
          <p className="text-xs text-white/40">
            © {new Date().getFullYear()} StudyShelf. Built for students.
          </p>
          <p className="text-xs text-white/40">
            Study smarter, not harder.
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
