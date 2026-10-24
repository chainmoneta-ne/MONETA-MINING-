export type ReferralStatus = 
  | 'INVITED'
  | 'REGISTERED'
  | 'VERIFIED'
  | 'QUALIFICATION_PENDING'
  | 'QUALIFIED'
  | 'REWARDED'
  | 'FRAUD_REVIEW'
  | 'REJECTED';

export interface ReferralRecord {
  id: string;
  name: string;
  monetaId: string;
  status: ReferralStatus;
  date: string;
  mintEarned: number;
}

export interface LeaderboardEntry {
  rank: number;
  previousRank: number;
  monetaId: string;
  username: string;
  qualifiedCount: number;
  isCurrentUser?: boolean;
}

export interface Transaction {
  id: string;
  timestamp: number;
  type: 'mining' | 'deposit' | 'withdraw' | 'referral' | 'subscription' | 'upgrade';
  amount: number;
  description: string;
  network?: string;
  txHash?: string;
}

export interface TelemetryState {
  hashRate: number; // MH/s
  sharesAccepted: number;
  sharesRejected: number;
  tempCelsius: number;
  powerUsageWatts: number;
  fanSpeedPercent: number;
  difficulty: string;
}

export interface UserProfile {
  username: string;
  monetaId: string;
  email: string;
  phone: string;
  walletBalance: number;
  totalEarned: number;
  minerLevel: number;
  isMining: boolean;
  miningProgress: number; // 0 to 1
  miningRemainingSeconds: number;
  autoMiningActive: boolean;
  activeBoosters: string[];
  isWalletConnected: boolean;
  walletAddress: string;
  connectedNetwork: 'BNB Smart Chain' | 'Ethereum' | null;
  connectedChainId: number | null;
  nativeAssetBalance: number;
}
