import React from 'react';
import { Cpu, Thermometer, Zap, CheckCircle2, Shield, Layers } from 'lucide-react';
import { TelemetryState } from '../types';

interface HashRateTelemetryProps {
  telemetry: TelemetryState;
  isMining: boolean;
}

export const HashRateTelemetry: React.FC<HashRateTelemetryProps> = ({ telemetry, isMining }) => {
  const efficiency = telemetry.sharesAccepted + telemetry.sharesRejected > 0
    ? ((telemetry.sharesAccepted / (telemetry.sharesAccepted + telemetry.sharesRejected)) * 100).toFixed(1)
    : '100.0';

  return (
    <div className="card mt-4 border border-[#262f42] bg-[#151922]">
      <div className="flex items-center justify-between mb-4 pb-3 border-b border-[#262f42]">
        <div className="flex items-center gap-2">
          <Cpu className="w-4 h-4 text-[#00e5ff]" />
          <h3 className="text-xs font-bold uppercase tracking-wider text-[#94a3b8]">
            HARDWARE TELEMETRY & CONSENSUS
          </h3>
        </div>
        <span className={`badge ${isMining ? 'badge-green' : 'badge-gray'}`}>
          {isMining ? 'ONLINE • 1 WORKER' : 'IDLE'}
        </span>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
        {/* Shares Accepted */}
        <div className="bg-[#1c2230] p-3 rounded-xl border border-[#262f42]/60">
          <div className="flex items-center gap-1.5 text-[#94a3b8] text-[11px] mb-1">
            <CheckCircle2 className="w-3.5 h-3.5 text-[#00e676]" />
            <span>SHARES ACCEPTED</span>
          </div>
          <div className="text-lg font-mono font-bold text-[#f1f5f9]">
            {isMining ? telemetry.sharesAccepted : 0}
            <span className="text-xs text-[#00e676] ml-1.5">({efficiency}%)</span>
          </div>
        </div>

        {/* Temperature */}
        <div className="bg-[#1c2230] p-3 rounded-xl border border-[#262f42]/60">
          <div className="flex items-center gap-1.5 text-[#94a3b8] text-[11px] mb-1">
            <Thermometer className="w-3.5 h-3.5 text-[#ffd700]" />
            <span>CORE TEMPERATURE</span>
          </div>
          <div className="text-lg font-mono font-bold text-[#f1f5f9]">
            {isMining ? telemetry.tempCelsius : 34}°C
            <span className="text-xs text-[#94a3b8] ml-1.5 font-normal">Optimal</span>
          </div>
        </div>

        {/* Power Draw */}
        <div className="bg-[#1c2230] p-3 rounded-xl border border-[#262f42]/60">
          <div className="flex items-center gap-1.5 text-[#94a3b8] text-[11px] mb-1">
            <Zap className="w-3.5 h-3.5 text-[#00e5ff]" />
            <span>POWER DRAW</span>
          </div>
          <div className="text-lg font-mono font-bold text-[#f1f5f9]">
            {isMining ? telemetry.powerUsageWatts : 5}W
            <span className="text-xs text-[#94a3b8] ml-1.5 font-normal">{isMining ? 'Fan 65%' : '0%'}</span>
          </div>
        </div>

        {/* Difficulty */}
        <div className="bg-[#1c2230] p-3 rounded-xl border border-[#262f42]/60">
          <div className="flex items-center gap-1.5 text-[#94a3b8] text-[11px] mb-1">
            <Shield className="w-3.5 h-3.5 text-[#94a3b8]" />
            <span>DIFFICULTY</span>
          </div>
          <div className="text-sm font-mono font-bold text-[#f1f5f9]">
            {telemetry.difficulty}
          </div>
        </div>

        {/* Algorithm */}
        <div className="bg-[#1c2230] p-3 rounded-xl border border-[#262f42]/60 col-span-1 sm:col-span-2">
          <div className="flex items-center gap-1.5 text-[#94a3b8] text-[11px] mb-1">
            <Layers className="w-3.5 h-3.5 text-[#00e5ff]" />
            <span>ALGORITHM & CONSENSUS</span>
          </div>
          <div className="text-sm font-mono font-bold text-gradient-moneta">
            Blake3-Moneta / ASIC-Resistant v2
          </div>
        </div>
      </div>
    </div>
  );
};
