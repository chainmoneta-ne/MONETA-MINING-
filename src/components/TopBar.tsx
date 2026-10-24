import React from 'react';
import { RefreshCw, Bell, Settings, ArrowLeft } from 'lucide-react';
import { UserProfile } from '../types';

interface TopBarProps {
  user: UserProfile;
  isSyncing: boolean;
  onSync: () => void;
  onOpenAnnouncements: () => void;
  onOpenSettings: () => void;
  currentView?: string;
  onBack?: () => void;
}

export const TopBar: React.FC<TopBarProps> = ({
  user,
  isSyncing,
  onSync,
  onOpenAnnouncements,
  onOpenSettings,
  currentView,
  onBack
}) => {
  const isSubScreen = currentView === 'announcements' || currentView === 'settings';

  return (
    <header className="sticky top-0 z-50 backdrop-blur-md bg-[#0e1117]/90 border-b border-[#262f42] px-4 py-2.5">
      <div className="max-w-[680px] mx-auto flex items-center justify-between">
        {/* Left: Brand or Back */}
        <div className="flex items-center gap-3">
          {isSubScreen && onBack ? (
            <button
              onClick={onBack}
              className="w-9 h-9 rounded-xl bg-[#1c2230] hover:bg-[#262f42] border border-[#262f42] flex items-center justify-center text-[#94a3b8] hover:text-[#00e5ff] transition-all"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
          ) : (
            <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-[#00e5ff] to-[#00e676] flex items-center justify-center font-bold text-[#0e1117] text-lg shadow-lg shadow-[#00e5ff]/20">
              M
            </div>
          )}

          <div>
            <div className="flex items-center gap-1.5">
              <span className="text-xs font-bold tracking-wider text-[#94a3b8] uppercase">
                MONETA CHAIN
              </span>
              <span className="w-1.5 h-1.5 rounded-full bg-[#00e676] animate-pulse"></span>
            </div>
            <div className="text-sm font-mono font-bold text-gradient-moneta">
              {user.walletBalance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} MINT
            </div>
          </div>
        </div>

        {/* Right: Actions matching MonetaApp.kt */}
        <div className="flex items-center gap-2">
          {/* Announcements Icon */}
          <button
            onClick={onOpenAnnouncements}
            title="Announcements"
            className={`relative w-9 h-9 rounded-xl bg-[#1c2230] hover:bg-[#262f42] border border-[#262f42] flex items-center justify-center transition-all ${
              currentView === 'announcements' ? 'text-[#00e5ff] border-[#00e5ff]' : 'text-[#94a3b8] hover:text-[#00e5ff]'
            }`}
          >
            <Bell className="w-4 h-4" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-[#00e5ff] shadow-[0_0_6px_#00e5ff]" />
          </button>

          {/* Settings Icon */}
          <button
            onClick={onOpenSettings}
            title="Settings"
            className={`w-9 h-9 rounded-xl bg-[#1c2230] hover:bg-[#262f42] border border-[#262f42] flex items-center justify-center transition-all ${
              currentView === 'settings' ? 'text-[#00e5ff] border-[#00e5ff]' : 'text-[#94a3b8] hover:text-[#00e5ff]'
            }`}
          >
            <Settings className="w-4 h-4" />
          </button>

          {/* Consensus Sync Icon */}
          <button
            onClick={onSync}
            disabled={isSyncing}
            title="Sync with Moneta Consensus"
            className="w-9 h-9 rounded-xl bg-[#1c2230] hover:bg-[#262f42] border border-[#262f42] flex items-center justify-center text-[#94a3b8] hover:text-[#00e5ff] transition-all disabled:opacity-50"
          >
            <RefreshCw className={`w-4 h-4 ${isSyncing ? 'animate-spin text-[#00e5ff]' : ''}`} />
          </button>
        </div>
      </div>
    </header>
  );
};
