import React from 'react';
import { Zap, ShieldCheck, Clock, Flame, Check, Sparkles } from 'lucide-react';
import { UserProfile } from '../types';

interface BoostScreenProps {
  user: UserProfile;
  onActivateBoost: (boostName: string, cost: number) => void;
  onShowNotice: (msg: string) => void;
}

export const BoostScreen: React.FC<BoostScreenProps> = ({
  user,
  onActivateBoost,
  onShowNotice
}) => {
  const boosters = [
    {
      id: 'auto-mining',
      title: '24/7 Cloud Auto-Mining',
      description: 'Continuous mining even when offline or app is closed. Never miss a block reward.',
      duration: '30 Days',
      multiplier: 'Uninterrupted',
      costMint: 250,
      badge: 'POPULAR',
      icon: Clock
    },
    {
      id: 'boost-2x',
      title: '2x Dual-Core Accelerator',
      description: 'Doubles hashing power and increases accepted shares probability by 100%.',
      duration: '24 Hours',
      multiplier: '2.0x HASH',
      costMint: 80,
      badge: 'FAST',
      icon: Zap
    },
    {
      id: 'boost-5x',
      title: '5x Quantum Overclock',
      description: 'Extreme compute booster. Five times hash speed on Blake3 consensus.',
      duration: '48 Hours',
      multiplier: '5.0x HASH',
      costMint: 180,
      badge: 'MAX POWER',
      icon: Flame
    }
  ];

  return (
    <div className="space-y-4 px-4 pt-4 pb-12">
      {/* Title */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-[#f1f5f9]">BOOSTERS</h1>
          <p className="text-xs text-[#94a3b8]">Multiply mining output & unlock automated cycles</p>
        </div>
        <div className="badge badge-gold flex items-center gap-1.5">
          <Sparkles className="w-3.5 h-3.5" />
          <span>INSTANT ACTIVATION</span>
        </div>
      </div>

      {/* Boosters List */}
      <div className="space-y-3">
        {boosters.map((b) => {
          const Icon = b.icon;
          const isActive = user.activeBoosters.includes(b.id) || (b.id === 'auto-mining' && user.autoMiningActive);
          const canAfford = user.walletBalance >= b.costMint;

          return (
            <div
              key={b.id}
              className={`card bg-[#151922] border p-5 transition-all ${
                isActive
                  ? 'border-[#00e676] bg-[#00e676]/5 shadow-[0_0_15px_rgba(0,230,118,0.1)]'
                  : 'border-[#262f42] hover:border-[#384358]'
              }`}
            >
              <div className="flex items-start justify-between gap-3 mb-2">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-[#1c2230] border border-[#262f42] flex items-center justify-center text-[#ffd700]">
                    <Icon className="w-5 h-5 text-[#ffd700]" />
                  </div>
                  <div>
                    <h3 className="text-sm font-bold text-[#f1f5f9]">{b.title}</h3>
                    <div className="flex items-center gap-2 mt-0.5">
                      <span className="badge badge-blue text-[10px] py-0 px-2">{b.multiplier}</span>
                      <span className="text-[11px] text-[#94a3b8]">{b.duration}</span>
                    </div>
                  </div>
                </div>
                <span className="badge badge-gold text-[10px]">{b.badge}</span>
              </div>

              <p className="text-xs text-[#94a3b8] mt-2 mb-4 leading-relaxed">
                {b.description}
              </p>

              <div className="flex items-center justify-between pt-3 border-t border-[#262f42]">
                <div>
                  <span className="text-[10px] uppercase font-bold text-[#94a3b8] block">Price</span>
                  <span className="text-sm font-mono font-bold text-gradient-gold">
                    {b.costMint} MINT
                  </span>
                </div>

                <button
                  onClick={() => onActivateBoost(b.id, b.costMint)}
                  disabled={isActive || !canAfford}
                  className={`btn-primary py-2 px-4 text-xs ${
                    isActive ? 'bg-[#00e676] text-black cursor-default' : ''
                  }`}
                >
                  {isActive ? (
                    <span className="flex items-center gap-1.5">
                      <Check className="w-4 h-4" /> ACTIVE
                    </span>
                  ) : canAfford ? (
                    'Activate Booster'
                  ) : (
                    'Insufficient MINT'
                  )}
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
