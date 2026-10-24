import React, { useState } from 'react';
import { 
  Lock, 
  Mail, 
  Smartphone, 
  User, 
  Gift, 
  Wallet,
  ArrowRight,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';
import { OtpModal } from '../components/OtpModal';
import { OrigamiCubeLoader } from '../components/OrigamiCubeLoader';

interface AuthScreenProps {
  onLoginSuccess: (userData: { username: string; email: string; phone: string; monetaId: string }) => void;
  onOpenTrustWallet: () => void;
}

export const AuthScreen: React.FC<AuthScreenProps> = ({
  onLoginSuccess,
  onOpenTrustWallet
}) => {
  const [isSignUp, setIsSignUp] = useState(false);
  const [username, setUsername] = useState('@moneta_miner');
  const [email, setEmail] = useState('miner@moneta.network');
  const [phone, setPhone] = useState('+1 (555) 019-2834');
  const [password, setPassword] = useState('••••••••');
  const [confirmPassword, setConfirmPassword] = useState('••••••••');
  const [referralCode, setReferralCode] = useState('MNT-8K4P7Q2X');

  const [isPhoneVerified, setIsPhoneVerified] = useState(false);
  const [isOtpModalOpen, setIsOtpModalOpen] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isConnecting, setIsConnecting] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (isSignUp && !isPhoneVerified) {
      setError('Please verify your phone number via SMS OTP before registration.');
      setIsOtpModalOpen(true);
      return;
    }

    if (isSignUp && password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    setIsConnecting(true);
    setTimeout(() => {
      setIsConnecting(false);
      onLoginSuccess({
        username: username.startsWith('@') ? username : `@${username}`,
        email,
        phone,
        monetaId: 'MNT-' + Math.random().toString(36).substring(2, 10).toUpperCase()
      });
    }, 1500);
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-[#0e1117] relative overflow-hidden">
      {/* Background glowing orbs */}
      <div className="absolute -top-40 -left-40 w-96 h-96 bg-[#00e5ff]/10 rounded-full blur-[100px] pointer-events-none" />
      <div className="absolute -bottom-40 -right-40 w-96 h-96 bg-[#00e676]/10 rounded-full blur-[100px] pointer-events-none" />

      <div className="w-full max-w-[440px] card bg-[#151922] border-[#262f42] p-7 shadow-2xl relative z-10">
        {/* Brand Logo */}
        <div className="text-center mb-6">
          <div className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-[#00e5ff] to-[#00e676] flex items-center justify-center font-bold text-[#0e1117] text-2xl mx-auto shadow-lg shadow-[#00e5ff]/20 mb-3">
            M
          </div>
          <h1 className="text-2xl font-bold tracking-tight text-[#f1f5f9]">
            MONETA MINING
          </h1>
          <p className="text-xs text-[#94a3b8] mt-1">
            Simple • Secure • Powerful Mining Experience
          </p>
        </div>

        {/* Tab Toggle */}
        <div className="flex bg-[#1c2230] p-1 rounded-xl border border-[#262f42] mb-6">
          <button
            type="button"
            onClick={() => {
              setIsSignUp(false);
              setError(null);
            }}
            className={`flex-1 py-2 text-xs font-bold rounded-lg transition-all ${
              !isSignUp ? 'bg-[#00e5ff] text-[#0e1117] shadow-sm' : 'text-[#94a3b8] hover:text-white'
            }`}
          >
            SIGN IN
          </button>
          <button
            type="button"
            onClick={() => {
              setIsSignUp(true);
              setError(null);
            }}
            className={`flex-1 py-2 text-xs font-bold rounded-lg transition-all ${
              isSignUp ? 'bg-[#00e5ff] text-[#0e1117] shadow-sm' : 'text-[#94a3b8] hover:text-white'
            }`}
          >
            CREATE ACCOUNT
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-3.5">
          {isSignUp && (
            <div>
              <label className="text-[11px] font-bold text-[#94a3b8] uppercase block mb-1">
                Username
              </label>
              <div className="relative">
                <User className="w-4 h-4 text-[#94a3b8] absolute left-3 top-3" />
                <input
                  type="text"
                  required
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="@yourhandle"
                  className="w-full bg-[#1c2230] border border-[#262f42] rounded-xl pl-9 pr-3 py-2.5 text-xs text-[#f1f5f9] outline-none focus:border-[#00e5ff]"
                />
              </div>
            </div>
          )}

          <div>
            <label className="text-[11px] font-bold text-[#94a3b8] uppercase block mb-1">
              Email Address
            </label>
            <div className="relative">
              <Mail className="w-4 h-4 text-[#94a3b8] absolute left-3 top-3" />
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="name@domain.com"
                className="w-full bg-[#1c2230] border border-[#262f42] rounded-xl pl-9 pr-3 py-2.5 text-xs text-[#f1f5f9] outline-none focus:border-[#00e5ff]"
              />
            </div>
          </div>

          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="text-[11px] font-bold text-[#94a3b8] uppercase block">
                Mobile Phone
              </label>
              {isSignUp && (
                <span className="text-[10px] text-[#00e5ff]">SMS OTP Required</span>
              )}
            </div>
            <div className="relative flex gap-2">
              <div className="relative flex-1">
                <Smartphone className="w-4 h-4 text-[#94a3b8] absolute left-3 top-3" />
                <input
                  type="tel"
                  required
                  value={phone}
                  onChange={(e) => {
                    setPhone(e.target.value);
                    setIsPhoneVerified(false);
                  }}
                  placeholder="+1 (555) 000-0000"
                  className="w-full bg-[#1c2230] border border-[#262f42] rounded-xl pl-9 pr-3 py-2.5 text-xs font-mono text-[#f1f5f9] outline-none focus:border-[#00e5ff]"
                />
              </div>
              {isSignUp && (
                <button
                  type="button"
                  onClick={() => setIsOtpModalOpen(true)}
                  className={`px-3 py-2 text-xs font-bold rounded-xl border transition-all ${
                    isPhoneVerified
                      ? 'bg-[#00e676]/10 border-[#00e676] text-[#00e676]'
                      : 'bg-[#00e5ff]/10 border-[#00e5ff] text-[#00e5ff] hover:bg-[#00e5ff]/20'
                  }`}
                >
                  {isPhoneVerified ? (
                    <span className="flex items-center gap-1">
                      <CheckCircle2 className="w-3.5 h-3.5" /> Verified
                    </span>
                  ) : (
                    'Verify OTP'
                  )}
                </button>
              )}
            </div>
          </div>

          <div>
            <label className="text-[11px] font-bold text-[#94a3b8] uppercase block mb-1">
              Password
            </label>
            <div className="relative">
              <Lock className="w-4 h-4 text-[#94a3b8] absolute left-3 top-3" />
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full bg-[#1c2230] border border-[#262f42] rounded-xl pl-9 pr-3 py-2.5 text-xs text-[#f1f5f9] outline-none focus:border-[#00e5ff]"
              />
            </div>
          </div>

          {isSignUp && (
            <div>
              <label className="text-[11px] font-bold text-[#94a3b8] uppercase block mb-1">
                Confirm Password
              </label>
              <div className="relative">
                <Lock className="w-4 h-4 text-[#94a3b8] absolute left-3 top-3" />
                <input
                  type="password"
                  required
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full bg-[#1c2230] border border-[#262f42] rounded-xl pl-9 pr-3 py-2.5 text-xs text-[#f1f5f9] outline-none focus:border-[#00e5ff]"
                />
              </div>
            </div>
          )}

          {isSignUp && (
            <div>
              <label className="text-[11px] font-bold text-[#94a3b8] uppercase block mb-1">
                Referral Code (Optional)
              </label>
              <div className="relative">
                <Gift className="w-4 h-4 text-[#ffd700] absolute left-3 top-3" />
                <input
                  type="text"
                  value={referralCode}
                  onChange={(e) => setReferralCode(e.target.value.toUpperCase())}
                  placeholder="MNT-XXXXXX"
                  className="w-full bg-[#1c2230] border border-[#262f42] rounded-xl pl-9 pr-3 py-2.5 text-xs font-mono text-[#f1f5f9] outline-none focus:border-[#00e5ff]"
                />
              </div>
              <span className="text-[10px] text-[#94a3b8] mt-1 block">
                Receive +50 MINT onboarding bonus upon qualification.
              </span>
            </div>
          )}

          {error && (
            <div className="flex items-center gap-1.5 text-xs text-[#ff3d71] bg-[#ff3d71]/10 p-2.5 rounded-xl border border-[#ff3d71]/20">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          <button type="submit" className="btn-primary w-full py-3 mt-2 text-sm">
            <span>{isSignUp ? 'Create Moneta Account' : 'Sign In to Node'}</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </form>

        {/* Web3 Alternative */}
        <div className="mt-5 pt-4 border-t border-[#262f42] text-center">
          <p className="text-[11px] text-[#94a3b8] mb-3">Or connect directly via Web3</p>
          <button
            type="button"
            onClick={onOpenTrustWallet}
            className="btn-secondary w-full py-2.5 text-xs flex items-center justify-center gap-2"
          >
            <Wallet className="w-4 h-4 text-[#00e5ff]" />
            <span>Connect with Trust Wallet</span>
          </button>
        </div>
      </div>

      {/* OTP Verification Modal */}
      <OtpModal
        phoneNumber={phone}
        isOpen={isOtpModalOpen}
        onClose={() => setIsOtpModalOpen(false)}
        onSuccess={() => {
          setIsPhoneVerified(true);
          setError(null);
        }}
      />

      {/* 3D Isometric Origami Loader during authentication */}
      {isConnecting && (
        <OrigamiCubeLoader
          size={80}
          text="Connecting to MONETA CHAIN..."
          fullScreen={true}
        />
      )}
    </div>
  );
};
