import React, { useState, useEffect, useRef } from 'react';
import { Smartphone, ShieldCheck, AlertCircle, X, RotateCw } from 'lucide-react';
import { monetaApi } from '../services/api';

interface OtpModalProps {
  phoneNumber: string;
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export const OtpModal: React.FC<OtpModalProps> = ({
  phoneNumber,
  isOpen,
  onClose,
  onSuccess
}) => {
  const [digits, setDigits] = useState<string[]>(['', '', '', '', '', '']);
  const [countdown, setCountdown] = useState<number>(60);
  const [isVerifying, setIsVerifying] = useState<boolean>(false);
  const [isResending, setIsResending] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const inputsRef = useRef<(HTMLInputElement | null)[]>([]);

  useEffect(() => {
    if (isOpen) {
      setDigits(['', '', '', '', '', '']);
      setCountdown(60);
      setErrorMessage(null);
      setTimeout(() => inputsRef.current[0]?.focus(), 150);
    }
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen || countdown <= 0) return;
    const timer = setInterval(() => {
      setCountdown((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);
    return () => clearInterval(timer);
  }, [isOpen, countdown]);

  if (!isOpen) return null;

  const handleChange = (index: number, value: string) => {
    const clean = value.replace(/\D/g, '');
    if (!clean) {
      const newDigits = [...digits];
      newDigits[index] = '';
      setDigits(newDigits);
      return;
    }

    const char = clean.slice(-1);
    const newDigits = [...digits];
    newDigits[index] = char;
    setDigits(newDigits);
    setErrorMessage(null);

    // Auto-advance
    if (index < 5 && char) {
      inputsRef.current[index + 1]?.focus();
    }

    // Auto-verify if all filled
    if (index === 5 && char) {
      const fullCode = newDigits.join('');
      triggerVerification(fullCode);
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Backspace' && !digits[index] && index > 0) {
      inputsRef.current[index - 1]?.focus();
    }
  };

  const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    e.preventDefault();
    const pasteData = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6);
    if (pasteData.length > 0) {
      const newDigits = [...digits];
      for (let i = 0; i < 6; i++) {
        newDigits[i] = pasteData[i] || '';
      }
      setDigits(newDigits);
      if (pasteData.length === 6) {
        triggerVerification(pasteData);
      } else {
        inputsRef.current[pasteData.length]?.focus();
      }
    }
  };

  const triggerVerification = async (code: string) => {
    setIsVerifying(true);
    setErrorMessage(null);
    try {
      const res = await monetaApi.verifyPhoneOtp(phoneNumber, code);
      if (res.success) {
        onSuccess();
        onClose();
      } else {
        setErrorMessage(res.error || 'Verification failed. Please check the code.');
      }
    } catch {
      setErrorMessage('Verification server unreachable. Please retry.');
    } finally {
      setIsVerifying(false);
    }
  };

  const handleResend = async () => {
    if (countdown > 0 || isResending) return;
    setIsResending(true);
    setErrorMessage(null);
    try {
      await monetaApi.requestPhoneOtp(phoneNumber);
      setCountdown(60);
      setDigits(['', '', '', '', '', '']);
      inputsRef.current[0]?.focus();
    } catch {
      setErrorMessage('Failed to resend SMS. Try again.');
    } finally {
      setIsResending(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="card w-full max-w-[420px] bg-[#151922] border-[#262f42] p-6 relative shadow-2xl">
        {/* Close Button */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-[#94a3b8] hover:text-white p-1 rounded-lg hover:bg-[#1c2230]"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Header Icon */}
        <div className="w-12 h-12 rounded-2xl bg-[#00e5ff]/10 border border-[#00e5ff]/30 text-[#00e5ff] flex items-center justify-center mx-auto mb-4">
          <Smartphone className="w-6 h-6" />
        </div>

        <h3 className="text-xl font-bold text-center text-[#f1f5f9]">
          Phone Verification
        </h3>
        <p className="text-xs text-[#94a3b8] text-center mt-1">
          Enter the 6-digit authentication PIN sent via SMS to:
        </p>
        <p className="text-xs font-mono font-bold text-center text-[#00e5ff] mt-0.5">
          {phoneNumber || '+1 (555) 019-2834'}
        </p>

        {/* 6 Digit Inputs */}
        <div className="flex justify-center gap-2.5 my-6">
          {digits.map((digit, idx) => (
            <input
              key={idx}
              ref={(el) => (inputsRef.current[idx] = el)}
              type="text"
              inputMode="numeric"
              maxLength={1}
              value={digit}
              onChange={(e) => handleChange(idx, e.target.value)}
              onKeyDown={(e) => handleKeyDown(idx, e)}
              onPaste={handlePaste}
              className="w-11 h-13 text-center text-xl font-mono font-bold bg-[#1c2230] text-[#f1f5f9] border border-[#262f42] rounded-xl focus:border-[#00e5ff] focus:ring-1 focus:ring-[#00e5ff] outline-none transition-all shadow-inner"
            />
          ))}
        </div>

        {/* Error message */}
        {errorMessage && (
          <div className="flex items-center gap-2 bg-[#ff3d71]/10 border border-[#ff3d71]/30 p-2.5 rounded-xl text-[#ff3d71] text-xs mb-4">
            <AlertCircle className="w-4 h-4 shrink-0" />
            <span>{errorMessage}</span>
          </div>
        )}

        {/* Submit Button */}
        <button
          onClick={() => triggerVerification(digits.join(''))}
          disabled={isVerifying || digits.some((d) => !d)}
          className="btn-primary w-full py-3 mb-4"
        >
          {isVerifying ? (
            <div className="flex items-center gap-2">
              <RotateCw className="w-4 h-4 animate-spin" />
              <span>Verifying PIN...</span>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <ShieldCheck className="w-4 h-4" />
              <span>Verify & Continue</span>
            </div>
          )}
        </button>

        {/* Resend & Test hint */}
        <div className="flex items-center justify-between text-xs text-[#94a3b8] pt-2 border-t border-[#262f42]">
          <span>
            {countdown > 0 ? (
              `Resend in ${countdown}s`
            ) : (
              <button
                onClick={handleResend}
                disabled={isResending}
                className="text-[#00e5ff] hover:underline font-semibold"
              >
                Resend Code
              </button>
            )}
          </span>
          <span className="font-mono text-[10px] text-[#94a3b8]/80">
            Demo code: <strong className="text-[#00e676]">123456</strong>
          </span>
        </div>
      </div>
    </div>
  );
};
