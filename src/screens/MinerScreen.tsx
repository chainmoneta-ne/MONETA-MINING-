import React from 'react';
import { Pickaxe, ArrowUpRight, ShieldCheck, Zap, Server, Clock } from 'lucide-react';
import { CircularMiningProgress } from '../components/CircularMiningProgress';
import { HashRateTelemetry } from '../components/HashRateTelemetry';
import { UserProfile, TelemetryState } from '../types';

interface MinerScreenProps {
  user: UserProfile;
  telemetry: TelemetryState;
  onToggleMining: () => void;
  onUpgradeMiner: () => void;
  onShowNotice: (msg: string) => void;
}

export const MinerScreen: React.FC<MinerScreenProps> = ({
  user,
  telemetry,
  onToggleMining,
  onUpgradeMiner,
  onShowNotice
}) => {
  const upgradeCost = user.minerLevel * 150;
  const canAffordUpgrade = user.walletBalance >= upgradeCost;

  return (
    <div className="space-y-4 px-4 pt-4 pb-12">
      {/* Top Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-[#f1f5f9]">NODE MINER</h1>
          <p className="text-xs text-[#94a3b8]">Blake3 Consensus • Proof-of-Compute Network</p>
        </div>
        <div className="badge badge-green flex items-center gap-1.5">
          <Server className="w-3.5 h-3.5" />
          <span>SYNCHRONIZED</span>
        </div>
      </div>

      {/* Main Circular Mining Gauge */}
      <div className="card bg-[#151922] border-[#262f42] p-5 shadow-xl">
        <CircularMiningProgress
          isMining={user.isMining}
          progress={user.miningProgress}
          remainingSeconds={user.miningRemainingSeconds}
          hashRate={telemetry.hashRate}
          estimatedYield={24.5 * (1 + (user.minerLevel - 1) * 0.25)}
          minerLevel={user.minerLevel}
          onToggleMining={onToggleMining}
        />
      </div>

      {/* Real-time Hardware Telemetry */}
      <HashRateTelemetry telemetry={telemetry} isMining={user.isMining} />

      {/* Miner Hardware Upgrade Card */}
      <div className="card bg-[#151922] border-[#262f42] p-5">
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-[#00e5ff]/10 border border-[#00e5ff]/30 text-[#00e5ff] flex items-center justify-center">
              <Zap className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-[#f1f5f9]">
                ASIC Core Accelerator (Tier {user.minerLevel})
              </h3>
              <p className="text-xs text-[#94a3b8]">
                Boosts base hash rate by +25% per level
              </p>
            </div>
          </div>
          <span className="badge badge-gold">LEVEL {user.minerLevel}</span>
        </div>

        <div className="flex items-center justify-between pt-2 border-t border-[#262f42]">
          <div>
            <span className="text-[11px] text-[#94a3b8] block">Upgrade Cost</span>
            <span className="text-sm font-mono font-bold text-gradient-gold">
              {upgradeCost} MINT
            </span>
          </div>

          <button
            onClick={onUpgradeMiner}
            disabled={!canAffordUpgrade}
            className="btn-primary py-2.5 px-4 text-xs font-bold"
          >
            {canAffordUpgrade ? `Upgrade to Tier ${user.minerLevel + 1}` : 'Insufficient MINT'}
          </button>
        </div>
      </div>
    </div>
  );
};
