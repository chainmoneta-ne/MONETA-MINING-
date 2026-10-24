import React from 'react';
import { Activity, Flame, ShieldCheck, Zap } from 'lucide-react';

interface CircularMiningProgressProps {
  isMining: boolean;
  progress: number; // 0 to 1
  remainingSeconds: number;
  hashRate: number; // MH/s
  estimatedYield: number;
  minerLevel: number;
  onToggleMining: () => void;
}

export const CircularMiningProgress: React.FC<CircularMiningProgressProps> = ({
  isMining,
  progress,
  remainingSeconds,
  hashRate,
  estimatedYield,
  minerLevel,
  onToggleMining
}) => {
  const size = 260;
  const strokeWidth = 12;
  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const clampedProgress = Math.min(Math.max(progress, 0), 1);
  const strokeDashoffset = circumference - clampedProgress * circumference;

  const formatTime = (seconds: number) => {
    const hrs = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    return `${hrs.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="flex flex-col items-center justify-center relative my-4">
      {/* Background glow when mining */}
      {isMining && (
        <div className="absolute w-[240px] h-[240px] rounded-full bg-gradient-to-tr from-[#00e5ff]/20 to-[#00e676]/20 blur-2xl pointer-events-none" />
      )}

      {/* Interactive Circular Progress Container */}
      <div className="relative cursor-pointer" onClick={onToggleMining} title="Click to start or stop mining">
        <svg width={size} height={size} className="transform -rotate-90">
          <defs>
            <linearGradient id="monetaMiningGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#00e5ff" />
              <stop offset="50%" stopColor="#00b0ff" />
              <stop offset="100%" stopColor="#00e676" />
            </linearGradient>
            <filter id="glowEffect" x="-20%" y="-20%" width="140%" height="140%">
              <feGaussianBlur stdDeviation="4" result="blur" />
              <feComposite in="SourceGraphic" in2="blur" operator="over" />
            </filter>
          </defs>

          {/* Background Track */}
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke="#1c2230"
            strokeWidth={strokeWidth}
            fill="transparent"
          />

          {/* Animated Progress Ring */}
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke="url(#monetaMiningGradient)"
            strokeWidth={strokeWidth}
            strokeDasharray={circumference}
            strokeDashoffset={strokeDashoffset}
            strokeLinecap="round"
            fill="transparent"
            style={{
              transition: 'stroke-dashoffset 0.8s cubic-bezier(0.4, 0, 0.2, 1)',
              filter: isMining ? 'url(#glowEffect)' : 'none'
            }}
          />
        </svg>

        {/* Outer Rotating Particles Orbit when mining */}
        {isMining && (
          <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
            <div className="w-[280px] h-[280px] rounded-full border border-dashed border-[#00e5ff]/30 animate-spin-slow" />
          </div>
        )}

        {/* Center Telemetry & Metrics Display */}
        <div className="absolute inset-0 flex flex-col items-center justify-center text-center p-6 select-none">
          {/* Mining Status Badge */}
          <div className="mb-2">
            {isMining ? (
              <span className="badge badge-green flex items-center gap-1.5 shadow-[0_0_10px_rgba(0,230,118,0.4)]">
                <span className="w-2 h-2 rounded-full bg-[#00e676] animate-ping" />
                MINING ACTIVE
              </span>
            ) : (
              <span className="badge badge-gray flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-[#94a3b8]" />
                STANDBY
              </span>
            )}
          </div>

          {/* Real-time Hash Rate */}
          <div className="font-mono text-3xl font-bold tracking-tight text-[#f1f5f9] flex items-baseline justify-center gap-1">
            <span>{isMining ? hashRate.toFixed(2) : '0.00'}</span>
            <span className="text-xs font-semibold text-[#00e5ff]">MH/s</span>
          </div>

          {/* Countdown & Progress */}
          <div className="text-xs font-mono text-[#94a3b8] mt-1">
            {isMining ? formatTime(remainingSeconds) : 'Ready to start'}
          </div>

          {/* Yield Projection */}
          <div className="mt-3 flex items-center gap-1.5 text-xs font-bold text-[#ffd700] bg-[#ffd700]/10 px-3 py-1 rounded-full border border-[#ffd700]/20">
            <Flame className="w-3.5 h-3.5 text-[#ffd700]" />
            +{estimatedYield.toFixed(2)} MINT / cycle
          </div>
        </div>
      </div>

      {/* Action Prompt */}
      <div className="mt-4 text-center">
        <button
          onClick={onToggleMining}
          className={`btn-primary w-52 py-3 shadow-lg ${
            isMining
              ? 'bg-gradient-to-r from-[#ff3d71] to-[#ff6b8b] text-white shadow-[#ff3d71]/30'
              : 'bg-gradient-moneta text-[#0e1117] shadow-[#00e5ff]/30'
          }`}
        >
          {isMining ? (
            <>
              <Activity className="w-4 h-4 animate-spin" />
              PAUSE MINING
            </>
          ) : (
            <>
              <Zap className="w-4 h-4 fill-current" />
              START MINING (LVL {minerLevel})
            </>
          )}
        </button>
        <p className="text-[11px] text-[#94a3b8] mt-2 font-mono">
          {isMining ? 'Blake3 Consensus • Proof of Compute active' : 'Click to initiate node worker session'}
        </p>
      </div>
    </div>
  );
};
