import React from 'react';
import { Pickaxe, Zap, Users, Wallet } from 'lucide-react';

export type NavTab = 'miner' | 'boost' | 'friends' | 'wallet';

interface BottomNavProps {
  currentTab: NavTab;
  onSelectTab: (tab: NavTab) => void;
  friendsNotificationCount?: number;
}

export const BottomNav: React.FC<BottomNavProps> = ({
  currentTab,
  onSelectTab,
  friendsNotificationCount = 0
}) => {
  const tabs = [
    { id: 'miner' as NavTab, label: 'MINER', icon: Pickaxe },
    { id: 'boost' as NavTab, label: 'BOOST', icon: Zap },
    { id: 'friends' as NavTab, label: 'FRIENDS', icon: Users, badge: friendsNotificationCount },
    { id: 'wallet' as NavTab, label: 'WALLET', icon: Wallet }
  ];

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-50 backdrop-blur-lg bg-[#0e1117]/90 border-t border-[#262f42] py-2 px-4">
      <div className="max-w-[680px] mx-auto flex items-center justify-around">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          const isActive = currentTab === tab.id;

          return (
            <button
              key={tab.id}
              onClick={() => onSelectTab(tab.id)}
              className={`relative flex flex-col items-center justify-center py-1 px-4 rounded-xl transition-all duration-200 ${
                isActive
                  ? 'text-[#00e5ff] scale-105'
                  : 'text-[#94a3b8] hover:text-[#f1f5f9]'
              }`}
            >
              <div className="relative">
                <Icon className={`w-5 h-5 mb-1 ${isActive ? 'stroke-[2.5px]' : 'stroke-2'}`} />
                {tab.badge && tab.badge > 0 ? (
                  <span className="absolute -top-1.5 -right-2 w-4 h-4 rounded-full bg-[#00e676] text-[#0e1117] text-[10px] font-bold flex items-center justify-center">
                    {tab.badge}
                  </span>
                ) : null}
              </div>
              <span
                className={`text-[11px] font-bold tracking-wider ${
                  isActive ? 'text-[#00e5ff]' : 'text-[#94a3b8]'
                }`}
              >
                {tab.label}
              </span>
              {isActive && (
                <span className="absolute bottom-0 w-8 h-0.5 rounded-full bg-[#00e5ff] shadow-[0_0_8px_#00e5ff]" />
              )}
            </button>
          );
        })}
      </div>
    </nav>
  );
};
