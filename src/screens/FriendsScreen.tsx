import React, { useState } from 'react';
import { 
  Copy, 
  Share2, 
  Trophy, 
  Award, 
  CheckCircle2, 
  Clock, 
  AlertTriangle, 
  XCircle, 
  Sparkles, 
  TrendingUp,
  UserCheck,
  ShieldCheck,
  ChevronRight
} from 'lucide-react';
import confetti from 'canvas-confetti';
import { ReferralRecord, LeaderboardEntry, UserProfile, ReferralStatus } from '../types';

interface FriendsScreenProps {
  user: UserProfile;
  referrals: ReferralRecord[];
  leaderboard: LeaderboardEntry[];
  onQualifyReferral?: (referralId: string) => void;
  onShowNotice: (msg: string) => void;
}

export const FriendsScreen: React.FC<FriendsScreenProps> = ({
  user,
  referrals,
  leaderboard,
  onQualifyReferral,
  onShowNotice
}) => {
  const [activeTab, setActiveTab] = useState<'global' | 'myRank'>('global');
  const [copied, setCopied] = useState(false);
  const [newRankAnnouncement, setNewRankAnnouncement] = useState<number | null>(null);

  // Compute metrics from actual referral state
  const invitedCount = referrals.length;
  const registeredCount = referrals.filter((r) => 
    ['REGISTERED', 'VERIFIED', 'QUALIFICATION_PENDING', 'QUALIFIED', 'REWARDED'].includes(r.status)
  ).length;
  const qualifiedCount = referrals.filter((r) => 
    ['QUALIFIED', 'REWARDED'].includes(r.status)
  ).length;
  const rewardedCount = referrals.filter((r) => r.status === 'REWARDED').length;
  const mintEarned = referrals.reduce((acc, r) => acc + (r.mintEarned || 0), 0);

  // Current user's leaderboard position
  const userRankEntry = leaderboard.find((entry) => entry.isCurrentUser) || {
    rank: 127,
    previousRank: 130,
    monetaId: user.monetaId,
    username: user.username,
    qualifiedCount: 23,
    isCurrentUser: true
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(user.monetaId);
    setCopied(true);
    onShowNotice(`Copied Moneta ID: ${user.monetaId}`);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleInvite = () => {
    const inviteUrl = `${window.location.origin}/?ref=${user.monetaId}`;
    if (navigator.share) {
      navigator.share({
        title: 'Join Moneta Mining',
        text: `Mine MINT with me on Moneta! Use my referral code: ${user.monetaId}`,
        url: inviteUrl
      }).catch(() => {});
    } else {
      navigator.clipboard.writeText(inviteUrl);
      onShowNotice(`Invite link copied to clipboard!`);
    }
  };

  // Simulate an automated backend qualification event to demonstrate ranking update
  const handleSimulateQualification = (refId: string) => {
    if (onQualifyReferral) {
      onQualifyReferral(refId);
      const newRank = Math.max(1, userRankEntry.rank - 3);
      setNewRankAnnouncement(newRank);
      confetti({
        particleCount: 80,
        spread: 60,
        origin: { y: 0.6 }
      });
      setTimeout(() => setNewRankAnnouncement(null), 5000);
    }
  };

  const getStatusBadge = (status: ReferralStatus) => {
    switch (status) {
      case 'QUALIFIED':
        return <span className="badge badge-green"><CheckCircle2 className="w-3 h-3" /> QUALIFIED</span>;
      case 'REWARDED':
        return <span className="badge badge-gold"><Award className="w-3 h-3" /> REWARDED</span>;
      case 'QUALIFICATION_PENDING':
        return <span className="badge badge-blue"><Clock className="w-3 h-3" /> PENDING</span>;
      case 'VERIFIED':
        return <span className="badge badge-blue"><UserCheck className="w-3 h-3" /> VERIFIED</span>;
      case 'REGISTERED':
        return <span className="badge badge-gray">REGISTERED</span>;
      case 'FRAUD_REVIEW':
        return <span className="badge badge-gold"><AlertTriangle className="w-3 h-3" /> FRAUD REVIEW</span>;
      case 'REJECTED':
        return <span className="badge badge-red"><XCircle className="w-3 h-3" /> REJECTED</span>;
      case 'INVITED':
      default:
        return <span className="badge badge-gray">INVITED</span>;
    }
  };

  const renderRankMedal = (rank: number) => {
    if (rank === 1) return <span className="text-xl">👑</span>;
    if (rank === 2) return <span className="text-xl">🥈</span>;
    if (rank === 3) return <span className="text-xl">🥉</span>;
    return <span className="font-mono text-sm font-bold text-[#94a3b8] w-6 text-center">{rank}</span>;
  };

  return (
    <div className="space-y-5 px-4 pt-4 pb-12">
      {/* Title */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-[#f1f5f9]">FRIENDS</h1>
          <p className="text-xs text-[#94a3b8]">Referral Leaderboard & Automated Ranking System</p>
        </div>
        <div className="badge badge-blue flex items-center gap-1">
          <ShieldCheck className="w-3.5 h-3.5" />
          <span>ANTI-FRAUD VERIFIED</span>
        </div>
      </div>

      {/* NEW RANK CELEBRATION BANNER */}
      {newRankAnnouncement && (
        <div className="card bg-gradient-to-r from-[#00e5ff]/20 via-[#00e676]/20 to-[#ffd700]/20 border border-[#00e5ff] p-4 text-center animate-bounce shadow-lg">
          <div className="text-xs font-bold text-[#00e5ff] uppercase tracking-widest flex items-center justify-center gap-1.5">
            <Sparkles className="w-4 h-4 text-[#ffd700]" />
            AUTOMATIC RANK PROMOTION
          </div>
          <div className="text-2xl font-bold text-[#f1f5f9] mt-1">
            🎉 NEW RANK <span className="text-[#00e5ff]">#{newRankAnnouncement}</span>
          </div>
          <div className="text-xs text-[#94a3b8] mt-0.5">
            Backend recalculated leaderboard via Qualified Referrals algorithm.
          </div>
        </div>
      )}

      {/* 1. MY MONETA ID CARD */}
      <div className="card bg-[#151922] border-[#262f42] p-5 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 right-0 w-32 h-32 bg-[#00e5ff]/5 rounded-full blur-2xl pointer-events-none" />
        <span className="text-xs font-bold uppercase tracking-wider text-[#94a3b8] block mb-1">
          MY MONETA ID
        </span>
        <div className="text-2xl font-mono font-bold text-gradient-moneta tracking-wider mb-4">
          {user.monetaId}
        </div>

        <div className="grid grid-cols-2 gap-3">
          <button
            onClick={handleCopy}
            className="btn-secondary py-3 text-sm flex items-center justify-center gap-2"
          >
            <Copy className="w-4 h-4 text-[#00e5ff]" />
            <span>{copied ? 'COPIED!' : 'COPY ID'}</span>
          </button>

          <button
            onClick={handleInvite}
            className="btn-primary py-3 text-sm flex items-center justify-center gap-2"
          >
            <Share2 className="w-4 h-4" />
            <span>INVITE FRIENDS</span>
          </button>
        </div>
      </div>

      {/* MY REFERRALS STATS BREAKDOWN */}
      <div className="card bg-[#151922] border-[#262f42] p-5">
        <div className="flex items-center justify-between mb-3 pb-2 border-b border-[#262f42]">
          <h2 className="text-xs font-bold uppercase tracking-wider text-[#94a3b8]">
            MY REFERRALS
          </h2>
          <span className="text-[11px] font-mono text-[#00e5ff]">
            100 MINT per Qualified
          </span>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-center mb-4">
          <div className="bg-[#1c2230] p-3 rounded-xl border border-[#262f42]">
            <div className="text-xs text-[#94a3b8]">Invited</div>
            <div className="text-xl font-mono font-bold text-[#f1f5f9] mt-0.5">{invitedCount}</div>
          </div>
          <div className="bg-[#1c2230] p-3 rounded-xl border border-[#262f42]">
            <div className="text-xs text-[#94a3b8]">Registered</div>
            <div className="text-xl font-mono font-bold text-[#f1f5f9] mt-0.5">{registeredCount}</div>
          </div>
          <div className="bg-[#1c2230] p-3 rounded-xl border border-[#00e676]/40 shadow-[0_0_12px_rgba(0,230,118,0.15)]">
            <div className="text-xs text-[#00e676] font-semibold">Qualified</div>
            <div className="text-xl font-mono font-bold text-[#00e676] mt-0.5">{qualifiedCount}</div>
          </div>
          <div className="bg-[#1c2230] p-3 rounded-xl border border-[#ffd700]/40">
            <div className="text-xs text-[#ffd700] font-semibold">Rewarded</div>
            <div className="text-xl font-mono font-bold text-[#ffd700] mt-0.5">{rewardedCount}</div>
          </div>
        </div>

        {/* MINT EARNED */}
        <div className="bg-gradient-to-r from-[#1c2230] to-[#212838] p-4 rounded-xl border border-[#262f42] flex items-center justify-between">
          <div>
            <div className="text-xs font-bold text-[#94a3b8] uppercase tracking-wider">
              MINT EARNED
            </div>
            <div className="text-xs text-[#94a3b8] mt-0.5">Authoritative Mint Ledger</div>
          </div>
          <div className="text-2xl font-mono font-bold text-gradient-gold">
            +{mintEarned} MINT
          </div>
        </div>
      </div>

      {/* REFERRAL LEADERBOARD */}
      <div className="card bg-[#151922] border-[#262f42] p-5">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <Trophy className="w-5 h-5 text-[#ffd700]" />
            <h2 className="text-sm font-bold uppercase tracking-wider text-[#f1f5f9]">
              REFERRAL LEADERBOARD
            </h2>
          </div>

          {/* Leaderboard Tabs: [ GLOBAL ] [ MY RANK ] */}
          <div className="flex bg-[#1c2230] p-1 rounded-xl border border-[#262f42]">
            <button
              onClick={() => setActiveTab('global')}
              className={`px-3 py-1.5 rounded-lg text-xs font-bold tracking-wider transition-all ${
                activeTab === 'global'
                  ? 'bg-[#00e5ff] text-[#0e1117] shadow-sm'
                  : 'text-[#94a3b8] hover:text-[#f1f5f9]'
              }`}
            >
              GLOBAL
            </button>
            <button
              onClick={() => setActiveTab('myRank')}
              className={`px-3 py-1.5 rounded-lg text-xs font-bold tracking-wider transition-all ${
                activeTab === 'myRank'
                  ? 'bg-[#00e5ff] text-[#0e1117] shadow-sm'
                  : 'text-[#94a3b8] hover:text-[#f1f5f9]'
              }`}
            >
              MY RANK
            </button>
          </div>
        </div>

        {/* Ranking Rule Context */}
        <div className="text-[11px] text-[#94a3b8] bg-[#1c2230]/60 p-2.5 rounded-xl border border-[#262f42] mb-4">
          <strong className="text-[#f1f5f9]">Backend-Authoritative Metric:</strong> Ranked strictly by Verified Qualified Referrals with timestamp tie-breakers. Registrations and clicks do NOT count.
        </div>

        {/* Top Referrers List */}
        <div className="space-y-2">
          {leaderboard.slice(0, activeTab === 'global' ? 5 : 3).map((entry) => {
            const isMe = entry.isCurrentUser;
            return (
              <div
                key={entry.monetaId}
                className={`p-3.5 rounded-xl border flex items-center justify-between transition-all ${
                  isMe
                    ? 'bg-[#00e5ff]/10 border-[#00e5ff] shadow-[0_0_15px_rgba(0,229,255,0.15)]'
                    : 'bg-[#1c2230] border-[#262f42] hover:border-[#384358]'
                }`}
              >
                <div className="flex items-center gap-3">
                  <div className="flex items-center justify-center w-7">
                    {renderRankMedal(entry.rank)}
                  </div>
                  <div>
                    <div className="text-sm font-bold text-[#f1f5f9] flex items-center gap-1.5">
                      <span>{entry.username}</span>
                      {isMe && (
                        <span className="badge badge-blue text-[9px] py-0 px-1.5">YOU</span>
                      )}
                    </div>
                    <div className="text-xs font-mono text-[#94a3b8]">{entry.monetaId}</div>
                  </div>
                </div>

                <div className="text-right">
                  <span className="text-sm font-mono font-bold text-[#00e676]">
                    {entry.qualifiedCount} Qualified
                  </span>
                  <div className="text-[10px] text-[#94a3b8]">Verified Referrals</div>
                </div>
              </div>
            );
          })}
        </div>

        {/* YOUR RANK STICKY CARD */}
        <div className="mt-4 p-4 rounded-xl bg-gradient-to-r from-[#1c2230] to-[#212838] border border-[#00e5ff]/40 flex items-center justify-between">
          <div>
            <div className="text-xs font-bold text-[#00e5ff] uppercase tracking-wider flex items-center gap-1.5">
              <TrendingUp className="w-3.5 h-3.5" />
              YOUR CURRENT STANDING
            </div>
            <div className="text-2xl font-mono font-bold text-[#f1f5f9] mt-0.5">
              #{userRankEntry.rank}
            </div>
          </div>
          <div className="text-right">
            <div className="text-base font-mono font-bold text-[#00e676]">
              {userRankEntry.qualifiedCount} Qualified Referrals
            </div>
            <div className="text-[11px] text-[#94a3b8]">
              {userRankEntry.rank <= 100 ? 'Top 100 Achieved' : 'Need +2 to reach Top 100'}
            </div>
          </div>
        </div>
      </div>

      {/* REFERRAL ACTIVITY & AUDIT LOG */}
      <div className="card bg-[#151922] border-[#262f42] p-5">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xs font-bold uppercase tracking-wider text-[#94a3b8]">
            REFERRAL ACTIVITY
          </h2>
          <span className="text-xs text-[#94a3b8] font-mono">
            {referrals.length} Total Records
          </span>
        </div>

        <div className="space-y-2.5">
          {referrals.map((item) => (
            <div
              key={item.id}
              className="p-3 bg-[#1c2230] rounded-xl border border-[#262f42] flex items-center justify-between"
            >
              <div>
                <div className="text-sm font-semibold text-[#f1f5f9]">{item.name}</div>
                <div className="text-xs font-mono text-[#94a3b8]">{item.monetaId} • {item.date}</div>
              </div>

              <div className="flex items-center gap-2">
                {getStatusBadge(item.status)}
                {/* Qualification demo trigger for pending/registered */}
                {['REGISTERED', 'QUALIFICATION_PENDING', 'VERIFIED'].includes(item.status) && (
                  <button
                    onClick={() => handleSimulateQualification(item.id)}
                    className="btn-outline text-[11px] py-1 px-2.5"
                    title="Simulate backend qualification trigger"
                  >
                    Qualify
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
