import React, { useState } from 'react';
import { 
  ArrowLeft, 
  ChevronRight, 
  User, 
  Mail, 
  Shield, 
  Trash2, 
  Moon, 
  Sparkles, 
  Vibrate, 
  Info, 
  RefreshCw, 
  FileText, 
  HelpCircle, 
  Headphones, 
  AlertTriangle, 
  ShieldAlert, 
  Lock, 
  BarChart2, 
  Download, 
  Scale, 
  HardDrive, 
  CheckCircle2,
  Wallet,
  LogOut
} from 'lucide-react';
import { UserProfile } from '../types';

interface SettingsScreenProps {
  user: UserProfile;
  onBack: () => void;
  onDisconnectWallet: () => void;
  onLogout: () => void;
  onShowNotice: (msg: string) => void;
}

export const SettingsScreen: React.FC<SettingsScreenProps> = ({
  user,
  onBack,
  onDisconnectWallet,
  onLogout,
  onShowNotice
}) => {
  const [isDarkMode, setIsDarkMode] = useState(true);
  const [activeModalTitle, setActiveModalTitle] = useState<string | null>(null);
  const [modalDescription, setModalDescription] = useState<string>('');
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [showClearCacheDialog, setShowClearCacheDialog] = useState(false);
  const [showProfileDialog, setShowProfileDialog] = useState(false);

  const openComingSoon = (title: string, desc?: string) => {
    setActiveModalTitle(title);
    setModalDescription(
      desc || 'This feature requires secure authentication and backend confirmation. Proceeding will initialize secure API communication.'
    );
  };

  const handleClearCache = () => {
    setShowClearCacheDialog(false);
    onShowNotice('Temporary cache cleared successfully. 45 MB freed.');
  };

  const handleDeleteAccount = () => {
    setShowDeleteDialog(false);
    onLogout();
    onShowNotice('Account deletion request initiated.');
  };

  return (
    <div className="space-y-5 px-4 pt-4 pb-16">
      {/* Top Header */}
      <div className="flex items-center gap-3">
        <button
          onClick={onBack}
          className="w-9 h-9 rounded-xl bg-[#1c2230] border border-[#262f42] flex items-center justify-center text-[#94a3b8] hover:text-[#00e5ff] transition-all"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <div>
          <h1 className="text-xl font-bold tracking-tight text-[#f1f5f9]">SETTINGS</h1>
          <p className="text-xs text-[#94a3b8]">Account, preferences & protocol parameters</p>
        </div>
      </div>

      {/* 1. ACCOUNT */}
      <div className="space-y-2">
        <h2 className="text-[11px] font-bold text-[#94a3b8] uppercase tracking-wider pl-1">
          ACCOUNT
        </h2>
        <div className="card bg-[#151922] border-[#262f42] p-0 overflow-hidden divide-y divide-[#262f42]">
          <button
            onClick={() => setShowProfileDialog(true)}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div>
              <div className="text-sm font-semibold text-[#f1f5f9]">User Profile</div>
              <div className="text-xs text-[#94a3b8] mt-0.5 font-mono">
                {user.username} • {user.monetaId}
              </div>
            </div>
            <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
          </button>

          <button
            onClick={() => openComingSoon('Email Address', `Registered email: ${user.email}. Address is encrypted with AES-256-GCM on Moneta core.`)}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div>
              <div className="text-sm font-semibold text-[#f1f5f9]">Email Address</div>
              <div className="text-xs text-[#94a3b8] mt-0.5">{user.email || 'miningmoneta@gmail.com'}</div>
            </div>
            <div className="flex items-center gap-2">
              <span className="badge badge-green text-[10px]">SECURE</span>
              <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
            </div>
          </button>

          <button
            onClick={() => openComingSoon('Security & Two-Factor (2FA)')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">Security & Two-Factor (2FA)</div>
            <div className="flex items-center gap-2">
              <span className="badge badge-blue text-[10px]">ACTIVE</span>
              <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
            </div>
          </button>

          <button
            onClick={() => setShowDeleteDialog(true)}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#ff3d71]/10 transition-colors text-[#ff3d71]"
          >
            <div className="text-sm font-semibold">Delete Account</div>
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* 2. CONNECTED WALLET */}
      <div className="space-y-2">
        <h2 className="text-[11px] font-bold text-[#94a3b8] uppercase tracking-wider pl-1">
          CONNECTED WALLET
        </h2>
        <div className="card bg-[#151922] border-[#262f42] p-0 overflow-hidden divide-y divide-[#262f42]">
          {user.isWalletConnected ? (
            <>
              <div className="p-4 flex items-center justify-between">
                <div>
                  <div className="text-sm font-semibold text-[#f1f5f9]">Trust Wallet</div>
                  <div className="text-xs font-mono text-[#94a3b8] mt-0.5">
                    {user.walletAddress ? `${user.walletAddress.substring(0, 6)}••••${user.walletAddress.substring(user.walletAddress.length - 4)}` : '0xb6FE••••8342'}
                    <span className="block text-[11px] text-[#00e5ff] mt-0.5">
                      Network: {user.connectedNetwork || 'BNB Smart Chain'}
                    </span>
                  </div>
                </div>
                <span className="badge badge-green text-[10px]">● Connected</span>
              </div>
              <button
                onClick={onDisconnectWallet}
                className="w-full p-4 flex items-center justify-between text-left hover:bg-[#ff3d71]/10 transition-colors text-[#ff3d71]"
              >
                <div className="text-sm font-semibold">Disconnect</div>
                <ChevronRight className="w-4 h-4" />
              </button>
            </>
          ) : (
            <div className="p-4 flex items-center justify-between">
              <div>
                <div className="text-sm font-semibold text-[#f1f5f9]">Wallet</div>
                <div className="text-xs text-[#94a3b8] mt-0.5">Connect on Wallet screen</div>
              </div>
              <span className="badge badge-gray text-[10px]">Not Connected</span>
            </div>
          )}
        </div>
      </div>

      {/* 3. DISPLAY & PREFERENCES */}
      <div className="space-y-2">
        <h2 className="text-[11px] font-bold text-[#94a3b8] uppercase tracking-wider pl-1">
          DISPLAY & PREFERENCES
        </h2>
        <div className="card bg-[#151922] border-[#262f42] p-0 overflow-hidden divide-y divide-[#262f42]">
          <div className="p-4 flex items-center justify-between">
            <div>
              <div className="text-sm font-semibold text-[#f1f5f9]">Dark Theme</div>
              <div className="text-xs text-[#94a3b8]">Optimized OLED high contrast</div>
            </div>
            <button
              onClick={() => setIsDarkMode(!isDarkMode)}
              className={`w-12 h-6 rounded-full transition-colors relative p-0.5 ${
                isDarkMode ? 'bg-[#00e5ff]' : 'bg-[#262f42]'
              }`}
            >
              <div
                className={`w-5 h-5 rounded-full bg-[#0e1117] transition-transform ${
                  isDarkMode ? 'translate-x-6' : 'translate-x-0'
                }`}
              />
            </button>
          </div>

          <button
            onClick={() => openComingSoon('Animations', 'Configure GPU acceleration and canvas rendering for the 3D cube and circular mining gauge.')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">Animations</div>
            <div className="flex items-center gap-2">
              <span className="text-xs text-[#94a3b8]">Full</span>
              <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
            </div>
          </button>

          <button
            onClick={() => openComingSoon('Haptic Feedback', 'Toggle device vibration on hash share acceptance and cycle completions.')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">Haptic Feedback</div>
            <div className="flex items-center gap-2">
              <span className="text-xs text-[#00e676]">ON</span>
              <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
            </div>
          </button>
        </div>
      </div>

      {/* 4. APP INFORMATION & UPDATES */}
      <div className="space-y-2">
        <h2 className="text-[11px] font-bold text-[#94a3b8] uppercase tracking-wider pl-1">
          APP INFORMATION & UPDATES
        </h2>
        <div className="card bg-[#151922] border-[#262f42] p-0 overflow-hidden divide-y divide-[#262f42]">
          <div className="p-4 flex items-center justify-between">
            <div className="text-sm font-semibold text-[#f1f5f9]">MONETA MINING APP</div>
            <span className="text-xs font-mono text-[#94a3b8]">v1.0.0 (100)</span>
          </div>

          <button
            onClick={() => openComingSoon('Check for Updates', 'Checking Moneta GitHub and Vercel edge CDN. Your client is on the latest production release!')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors text-[#00e5ff]"
          >
            <div className="text-sm font-semibold">Check for Updates</div>
            <ChevronRight className="w-4 h-4" />
          </button>

          <button
            onClick={() => openComingSoon('Release Notes', 'v1.0.0 Release Notes:\n• Blake3 consensus miner integration\n• Automated circular progress telemetry\n• Trust Wallet multi-chain bridge\n• Anti-fraud referral engine')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">Release Notes</div>
            <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
          </button>
        </div>
      </div>

      {/* 5. HELP & SUPPORT */}
      <div className="space-y-2">
        <h2 className="text-[11px] font-bold text-[#94a3b8] uppercase tracking-wider pl-1">
          HELP & SUPPORT
        </h2>
        <div className="card bg-[#151922] border-[#262f42] p-0 overflow-hidden divide-y divide-[#262f42]">
          <button
            onClick={() => openComingSoon('Help Center & FAQs', 'Knowledge Base:\n1. How does proof-of-compute mining work?\n2. What is the minimum withdrawal?\n3. When are referral rewards qualified?')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">Help Center & FAQs</div>
            <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
          </button>

          <button
            onClick={() => openComingSoon('Contact Support', 'Official protocol contact: support@moneta.network or join our 24/7 community on Discord.')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">Contact Support</div>
            <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
          </button>

          <button
            onClick={() => openComingSoon('Report a Problem', 'Submit diagnostic telemetry logs directly to the core developers.')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">Report a Problem</div>
            <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
          </button>

          <button
            onClick={() => openComingSoon('Report Security Issue', 'Responsible disclosure program: submit vulnerabilities to security@moneta.network for bug bounty rewards.')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors text-[#ffd700]"
          >
            <div className="text-sm font-semibold">Report Security Issue</div>
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* 6. DATA & PRIVACY */}
      <div className="space-y-2">
        <h2 className="text-[11px] font-bold text-[#94a3b8] uppercase tracking-wider pl-1">
          DATA & PRIVACY
        </h2>
        <div className="card bg-[#151922] border-[#262f42] p-0 overflow-hidden divide-y divide-[#262f42]">
          <button
            onClick={() => openComingSoon('Privacy Controls')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">Privacy Controls</div>
            <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
          </button>

          <button
            onClick={() => openComingSoon('Analytics & Data Collection')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">Analytics & Data Collection</div>
            <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
          </button>

          <button
            onClick={() => openComingSoon('Export Account Data', 'Generates an encrypted JSON archive containing your mining session ledger, referral tree, and audit logs.')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">Export Account Data</div>
            <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
          </button>
        </div>
      </div>

      {/* 7. LEGAL & TRANSPARENCY */}
      <div className="space-y-2">
        <h2 className="text-[11px] font-bold text-[#94a3b8] uppercase tracking-wider pl-1">
          LEGAL & TRANSPARENCY
        </h2>
        <div className="card bg-[#151922] border-[#262f42] p-0 overflow-hidden divide-y divide-[#262f42]">
          <button
            onClick={() => openComingSoon('Terms of Service')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">Terms of Service</div>
            <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
          </button>

          <button
            onClick={() => openComingSoon('Privacy Policy')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">Privacy Policy</div>
            <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
          </button>

          <button
            onClick={() => openComingSoon('MINT Reward & Participation Rules')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">MINT Reward & Participation Rules</div>
            <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
          </button>

          <button
            onClick={() => openComingSoon('Wallet & Payment Disclosures')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">Wallet & Payment Disclosures</div>
            <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
          </button>

          <button
            onClick={() => openComingSoon('Risk Disclosure')}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#1c2230]/70 transition-colors"
          >
            <div className="text-sm font-semibold text-[#f1f5f9]">Risk Disclosure</div>
            <ChevronRight className="w-4 h-4 text-[#94a3b8]" />
          </button>
        </div>
      </div>

      {/* 8. STORAGE */}
      <div className="space-y-2">
        <h2 className="text-[11px] font-bold text-[#94a3b8] uppercase tracking-wider pl-1">
          STORAGE
        </h2>
        <div className="card bg-[#151922] border-[#262f42] p-0 overflow-hidden divide-y divide-[#262f42]">
          <div className="p-4 flex items-center justify-between">
            <div className="text-sm font-semibold text-[#f1f5f9]">App Storage Usage</div>
            <span className="text-xs text-[#94a3b8]">45 MB</span>
          </div>

          <button
            onClick={() => setShowClearCacheDialog(true)}
            className="w-full p-4 flex items-center justify-between text-left hover:bg-[#ffd700]/10 transition-colors text-[#ffd700]"
          >
            <div className="text-sm font-semibold">Clear Cache</div>
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Logout Button */}
      <div className="pt-2">
        <button
          onClick={onLogout}
          className="btn-secondary w-full py-3 text-xs flex items-center justify-center gap-2 border-[#ff3d71]/40 text-[#ff3d71] hover:bg-[#ff3d71]/10"
        >
          <LogOut className="w-4 h-4" />
          <span>Sign Out of Node</span>
        </button>
      </div>

      {/* USER PROFILE MODAL */}
      {showProfileDialog && (
        <div className="modal-overlay">
          <div className="card bg-[#151922] border-[#00e5ff] max-w-[420px] w-full p-6 shadow-2xl relative">
            <h3 className="text-base font-bold text-[#00e5ff] uppercase tracking-wider mb-4">
              USER PROFILE
            </h3>
            
            <div className="space-y-3 font-mono text-xs">
              <div className="bg-[#1c2230] p-3 rounded-xl border border-[#262f42]">
                <span className="text-[10px] text-[#94a3b8] uppercase block">Moneta ID</span>
                <span className="text-[#00e5ff] font-bold">{user.monetaId}</span>
              </div>
              <div className="bg-[#1c2230] p-3 rounded-xl border border-[#262f42]">
                <span className="text-[10px] text-[#94a3b8] uppercase block">Username</span>
                <span className="text-[#f1f5f9]">{user.username}</span>
              </div>
              <div className="bg-[#1c2230] p-3 rounded-xl border border-[#262f42]">
                <span className="text-[10px] text-[#94a3b8] uppercase block">Email</span>
                <span className="text-[#f1f5f9]">{user.email || 'miningmoneta@gmail.com'}</span>
              </div>
              <div className="bg-[#1c2230] p-3 rounded-xl border border-[#262f42]">
                <span className="text-[10px] text-[#94a3b8] uppercase block">Miner Tier</span>
                <span className="text-[#ffd700] font-bold">Tier {user.minerLevel}</span>
              </div>
              <div className="bg-[#1c2230] p-3 rounded-xl border border-[#262f42]">
                <span className="text-[10px] text-[#94a3b8] uppercase block">Lifetime Mined</span>
                <span className="text-[#00e676] font-bold">{user.totalEarned.toFixed(2)} MINT</span>
              </div>
            </div>

            <div className="mt-5">
              <button
                onClick={() => setShowProfileDialog(false)}
                className="btn-primary w-full py-2.5 text-xs font-bold"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* CLEAR CACHE DIALOG */}
      {showClearCacheDialog && (
        <div className="modal-overlay">
          <div className="card bg-[#151922] border-[#262f42] max-w-[400px] w-full p-6 shadow-2xl">
            <h3 className="text-base font-bold text-[#f1f5f9] mb-2">CLEAR CACHE</h3>
            <p className="text-xs text-[#94a3b8] leading-relaxed mb-6">
              This will clear temporary files to free up space. It will NOT delete your MINT balance, blockchain transactions, subscriptions, or wallet ownership.
            </p>
            <div className="flex gap-2">
              <button
                onClick={() => setShowClearCacheDialog(false)}
                className="btn-secondary flex-1 py-2.5 text-xs"
              >
                Cancel
              </button>
              <button
                onClick={handleClearCache}
                className="btn-primary flex-1 py-2.5 text-xs"
              >
                Clear Cache
              </button>
            </div>
          </div>
        </div>
      )}

      {/* DELETE ACCOUNT DIALOG */}
      {showDeleteDialog && (
        <div className="modal-overlay">
          <div className="card bg-[#151922] border-[#ff3d71] max-w-[400px] w-full p-6 shadow-2xl">
            <h3 className="text-base font-bold text-[#ff3d71] mb-2">DELETE ACCOUNT</h3>
            <p className="text-xs text-[#94a3b8] leading-relaxed mb-6">
              This permanently removes your MONETA account and associated account data, subject to applicable retention requirements.
              <br /><br />
              Are you sure you want to proceed?
            </p>
            <div className="flex gap-2">
              <button
                onClick={() => setShowDeleteDialog(false)}
                className="btn-secondary flex-1 py-2.5 text-xs"
              >
                Cancel
              </button>
              <button
                onClick={handleDeleteAccount}
                className="bg-[#ff3d71] hover:bg-[#ff3d71]/80 text-white font-bold rounded-xl px-4 py-2.5 text-xs flex-1 transition-all"
              >
                Proceed to Authenticate
              </button>
            </div>
          </div>
        </div>
      )}

      {/* INFORMATION MODAL */}
      {activeModalTitle && (
        <div className="modal-overlay">
          <div className="card bg-[#151922] border-[#00e5ff] max-w-[400px] w-full p-6 shadow-2xl">
            <h3 className="text-base font-bold text-[#00e5ff] uppercase tracking-wider mb-2">
              {activeModalTitle}
            </h3>
            <p className="text-xs text-[#94a3b8] whitespace-pre-line leading-relaxed mb-6">
              {modalDescription}
            </p>
            <button
              onClick={() => setActiveModalTitle(null)}
              className="btn-primary w-full py-2.5 text-xs font-bold"
            >
              Understood
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
