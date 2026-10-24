import { UserProfile, ReferralRecord, LeaderboardEntry, Transaction, TelemetryState } from '../types';

const STORAGE_KEYS = {
  USER: 'moneta_user_profile',
  REFERRALS: 'moneta_referrals',
  LEADERBOARD: 'moneta_leaderboard',
  TRANSACTIONS: 'moneta_transactions',
  MINING: 'moneta_mining_session'
};

const DEFAULT_USER: UserProfile = {
  username: '@moneta_miner',
  monetaId: 'MNT-8K4P7Q2X',
  email: 'miner@moneta.network',
  phone: '+1 (555) 019-2834',
  walletBalance: 650.00,
  totalEarned: 1240.50,
  minerLevel: 1,
  isMining: false,
  miningProgress: 0,
  miningRemainingSeconds: 0,
  autoMiningActive: false,
  activeBoosters: [],
  isWalletConnected: false,
  walletAddress: '',
  connectedNetwork: null,
  connectedChainId: null,
  nativeAssetBalance: 0
};

const DEFAULT_REFERRALS: ReferralRecord[] = [
  { id: 'ref-1', name: 'Alex Vance', monetaId: 'MNT-9X2L4K', status: 'QUALIFIED', date: '2026-10-22', mintEarned: 100 },
  { id: 'ref-2', name: 'Elena Rostova', monetaId: 'MNT-4F8J2P', status: 'QUALIFIED', date: '2026-10-21', mintEarned: 100 },
  { id: 'ref-3', name: 'Marcus Brody', monetaId: 'MNT-7T1Q9W', status: 'REWARDED', date: '2026-10-20', mintEarned: 100 },
  { id: 'ref-4', name: 'Sarah Chen', monetaId: 'MNT-3B5M8Z', status: 'QUALIFIED', date: '2026-10-19', mintEarned: 100 },
  { id: 'ref-5', name: 'David Kim', monetaId: 'MNT-6K9R2X', status: 'QUALIFIED', date: '2026-10-18', mintEarned: 100 },
  { id: 'ref-6', name: 'Sophia Miller', monetaId: 'MNT-2H4L7V', status: 'QUALIFIED', date: '2026-10-16', mintEarned: 100 },
  { id: 'ref-7', name: 'Jason Sterling', monetaId: 'MNT-8V1C3D', status: 'QUALIFICATION_PENDING', date: '2026-10-23', mintEarned: 0 },
  { id: 'ref-8', name: 'Amina Nour', monetaId: 'MNT-5X9P1L', status: 'VERIFIED', date: '2026-10-23', mintEarned: 0 },
  { id: 'ref-9', name: 'Liam O\'Connor', monetaId: 'MNT-1Z8Q4T', status: 'REGISTERED', date: '2026-10-24', mintEarned: 0 },
  { id: 'ref-10', name: 'Kenji Sato', monetaId: 'MNT-0P3K8R', status: 'FRAUD_REVIEW', date: '2026-10-24', mintEarned: 0 },
  { id: 'ref-11', name: 'Chloe Dubois', monetaId: 'MNT-4L7S2E', status: 'INVITED', date: '2026-10-24', mintEarned: 0 },
  { id: 'ref-12', name: 'Mateo Garcia', monetaId: 'MNT-9T2W5Q', status: 'INVITED', date: '2026-10-24', mintEarned: 0 }
];

const DEFAULT_LEADERBOARD: LeaderboardEntry[] = [
  { rank: 1, previousRank: 1, monetaId: 'MNT-X94B2C', username: 'SatoshiNode', qualifiedCount: 124 },
  { rank: 2, previousRank: 2, monetaId: 'MNT-7F1Q8X', username: 'CryptoTitan', qualifiedCount: 97 },
  { rank: 3, previousRank: 3, monetaId: 'MNT-3M9Z4P', username: 'AuraMiner', qualifiedCount: 84 },
  { rank: 4, previousRank: 5, monetaId: 'MNT-5K2L9W', username: 'BlockNexus', qualifiedCount: 72 },
  { rank: 5, previousRank: 4, monetaId: 'MNT-8H6R3Y', username: 'CyberForge', qualifiedCount: 68 },
  { rank: 6, previousRank: 7, monetaId: 'MNT-2N8T5X', username: 'QuantumRig', qualifiedCount: 54 },
  { rank: 7, previousRank: 6, monetaId: 'MNT-4P1K9L', username: 'GenesisCore', qualifiedCount: 49 },
  { rank: 8, previousRank: 9, monetaId: 'MNT-9V3M2R', username: 'HashMaster', qualifiedCount: 42 },
  { rank: 9, previousRank: 8, monetaId: 'MNT-1B8X7C', username: 'VortexMining', qualifiedCount: 38 },
  { rank: 10, previousRank: 10, monetaId: 'MNT-6L4Q1S', username: 'ZenithChain', qualifiedCount: 35 },
  // Current user rank
  { rank: 127, previousRank: 130, monetaId: 'MNT-8K4P7Q2X', username: '@moneta_miner', qualifiedCount: 23, isCurrentUser: true }
];

const DEFAULT_TRANSACTIONS: Transaction[] = [
  { id: 'tx-101', timestamp: Date.now() - 3600000 * 2, type: 'referral', amount: 100, description: 'Referral reward: Elena Rostova', txHash: '0x9fa4...3821' },
  { id: 'tx-102', timestamp: Date.now() - 3600000 * 8, type: 'mining', amount: 24.50, description: '4-hour mining cycle payout', txHash: '0x3cb1...8842' },
  { id: 'tx-103', timestamp: Date.now() - 3600000 * 24, type: 'referral', amount: 100, description: 'Referral reward: Sarah Chen', txHash: '0x7e8b...199a' },
  { id: 'tx-104', timestamp: Date.now() - 3600000 * 48, type: 'deposit', amount: 50.00, description: 'Initial onboarding grant', txHash: '0x1ca2...992f' }
];

export const loadUserProfile = (): UserProfile => {
  try {
    const data = localStorage.getItem(STORAGE_KEYS.USER);
    return data ? { ...DEFAULT_USER, ...JSON.parse(data) } : DEFAULT_USER;
  } catch {
    return DEFAULT_USER;
  }
};

export const saveUserProfile = (profile: UserProfile): void => {
  try {
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(profile));
  } catch (e) {
    console.error('Failed to save user profile', e);
  }
};

export const loadReferrals = (): ReferralRecord[] => {
  try {
    const data = localStorage.getItem(STORAGE_KEYS.REFERRALS);
    return data ? JSON.parse(data) : DEFAULT_REFERRALS;
  } catch {
    return DEFAULT_REFERRALS;
  }
};

export const saveReferrals = (referrals: ReferralRecord[]): void => {
  try {
    localStorage.setItem(STORAGE_KEYS.REFERRALS, JSON.stringify(referrals));
  } catch (e) {
    console.error('Failed to save referrals', e);
  }
};

export const loadLeaderboard = (): LeaderboardEntry[] => {
  try {
    const data = localStorage.getItem(STORAGE_KEYS.LEADERBOARD);
    return data ? JSON.parse(data) : DEFAULT_LEADERBOARD;
  } catch {
    return DEFAULT_LEADERBOARD;
  }
};

export const saveLeaderboard = (leaderboard: LeaderboardEntry[]): void => {
  try {
    localStorage.setItem(STORAGE_KEYS.LEADERBOARD, JSON.stringify(leaderboard));
  } catch (e) {
    console.error('Failed to save leaderboard', e);
  }
};

export const loadTransactions = (): Transaction[] => {
  try {
    const data = localStorage.getItem(STORAGE_KEYS.TRANSACTIONS);
    return data ? JSON.parse(data) : DEFAULT_TRANSACTIONS;
  } catch {
    return DEFAULT_TRANSACTIONS;
  }
};

export const saveTransactions = (txs: Transaction[]): void => {
  try {
    localStorage.setItem(STORAGE_KEYS.TRANSACTIONS, JSON.stringify(txs));
  } catch (e) {
    console.error('Failed to save transactions', e);
  }
};
