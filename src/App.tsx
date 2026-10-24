import React, { useState, useEffect } from 'react';
import { TopBar } from './components/TopBar';
import { BottomNav, NavTab } from './components/BottomNav';
import { MinerScreen } from './screens/MinerScreen';
import { BoostScreen } from './screens/BoostScreen';
import { FriendsScreen } from './screens/FriendsScreen';
import { WalletScreen } from './screens/WalletScreen';
import { AuthScreen } from './screens/AuthScreen';
import { TrustWalletModal } from './components/TrustWalletModal';
import { 
  loadUserProfile, 
  saveUserProfile, 
  loadReferrals, 
  saveReferrals, 
  loadLeaderboard, 
  saveLeaderboard, 
  loadTransactions, 
  saveTransactions 
} from './services/storage';
import { UserProfile, ReferralRecord, LeaderboardEntry, Transaction, TelemetryState } from './types';

export const App: React.FC = () => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(true);
  const [currentTab, setCurrentTab] = useState<NavTab>('miner');
  const [user, setUser] = useState<UserProfile>(loadUserProfile);
  const [referrals, setReferrals] = useState<ReferralRecord[]>(loadReferrals);
  const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>(loadLeaderboard);
  const [transactions, setTransactions] = useState<Transaction[]>(loadTransactions);

  const [isTrustWalletModalOpen, setIsTrustWalletModalOpen] = useState<boolean>(false);
  const [isSyncing, setIsSyncing] = useState<boolean>(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  // Live Hardware Telemetry
  const [telemetry, setTelemetry] = useState<TelemetryState>({
    hashRate: 14.85,
    sharesAccepted: 482,
    sharesRejected: 0,
    tempCelsius: 52,
    powerUsageWatts: 85,
    fanSpeedPercent: 65,
    difficulty: '4.29G'
  });

  // Save changes to storage
  useEffect(() => {
    saveUserProfile(user);
  }, [user]);

  useEffect(() => {
    saveReferrals(referrals);
  }, [referrals]);

  useEffect(() => {
    saveLeaderboard(leaderboard);
  }, [leaderboard]);

  useEffect(() => {
    saveTransactions(transactions);
  }, [transactions]);

  // Real-time Mining Loop
  useEffect(() => {
    if (!user.isMining) return;

    const interval = setInterval(() => {
      // Fluctuate hash rate slightly for realistic telemetry
      const delta = (Math.random() - 0.48) * 0.4;
      const baseHash = 14.85 * (1 + (user.minerLevel - 1) * 0.25) * (user.activeBoosters.includes('boost-2x') ? 2 : 1) * (user.activeBoosters.includes('boost-5x') ? 5 : 1);
      const currentHash = Math.max(8.0, baseHash + delta);

      setTelemetry((prev) => ({
        ...prev,
        hashRate: currentHash,
        sharesAccepted: prev.sharesAccepted + (Math.random() > 0.7 ? 1 : 0),
        tempCelsius: Math.min(68, Math.max(48, Math.round(52 + (currentHash - 14.85)))),
        powerUsageWatts: Math.round(85 + (currentHash - 14.85) * 2)
      }));

      setUser((prevUser) => {
        if (!prevUser.isMining) return prevUser;

        const totalCycleSeconds = 120; // Fast interactive demonstration cycle (2 mins)
        const remaining = prevUser.miningRemainingSeconds > 0 ? prevUser.miningRemainingSeconds - 1 : 0;
        const progress = 1 - remaining / totalCycleSeconds;

        if (remaining <= 0) {
          // Cycle complete - pay out reward
          const payoutAmount = 24.5 * (1 + (prevUser.minerLevel - 1) * 0.25);
          const newTx: Transaction = {
            id: 'tx-' + Date.now(),
            timestamp: Date.now(),
            type: 'mining',
            amount: payoutAmount,
            description: `Node Tier ${prevUser.minerLevel} Cycle Payout`,
            txHash: '0x' + Math.random().toString(16).substring(2, 10) + '...'
          };
          setTransactions((prevTxs) => [newTx, ...prevTxs]);
          showNotice(`🎉 Mining cycle completed! Received +${payoutAmount.toFixed(2)} MINT.`);

          return {
            ...prevUser,
            isMining: prevUser.autoMiningActive, // continue if auto-mining active
            miningProgress: 0,
            miningRemainingSeconds: prevUser.autoMiningActive ? totalCycleSeconds : 0,
            walletBalance: prevUser.walletBalance + payoutAmount,
            totalEarned: prevUser.totalEarned + payoutAmount
          };
        }

        return {
          ...prevUser,
          miningRemainingSeconds: remaining,
          miningProgress: progress
        };
      });
    }, 1000);

    return () => clearInterval(interval);
  }, [user.isMining, user.minerLevel, user.activeBoosters, user.autoMiningActive]);

  const showNotice = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => {
      setToastMessage((prev) => (prev === msg ? null : prev));
    }, 3500);
  };

  const handleSync = () => {
    setIsSyncing(true);
    setTimeout(() => {
      setIsSyncing(false);
      showNotice('Synchronized with Moneta Blake3 consensus.');
    }, 1000);
  };

  const handleToggleMining = () => {
    if (user.isMining) {
      setUser((prev) => ({
        ...prev,
        isMining: false,
        miningProgress: 0,
        miningRemainingSeconds: 0
      }));
      showNotice('Mining session paused.');
    } else {
      setUser((prev) => ({
        ...prev,
        isMining: true,
        miningProgress: 0.05,
        miningRemainingSeconds: 120
      }));
      showNotice('Node worker initialized. Blake3 compute active.');
    }
  };

  const handleUpgradeMiner = () => {
    const cost = user.minerLevel * 150;
    if (user.walletBalance < cost) {
      showNotice('Insufficient MINT to upgrade.');
      return;
    }

    setUser((prev) => ({
      ...prev,
      minerLevel: prev.minerLevel + 1,
      walletBalance: prev.walletBalance - cost
    }));

    const newTx: Transaction = {
      id: 'tx-' + Date.now(),
      timestamp: Date.now(),
      type: 'upgrade',
      amount: -cost,
      description: `Hardware Upgrade to Tier ${user.minerLevel + 1}`
    };
    setTransactions((prev) => [newTx, ...prev]);
    showNotice(`Upgraded to Tier ${user.minerLevel + 1}! Hash power increased by +25%.`);
  };

  const handleActivateBoost = (boostId: string, cost: number) => {
    if (user.walletBalance < cost) {
      showNotice('Insufficient MINT balance.');
      return;
    }

    setUser((prev) => ({
      ...prev,
      walletBalance: prev.walletBalance - cost,
      autoMiningActive: boostId === 'auto-mining' ? true : prev.autoMiningActive,
      activeBoosters: prev.activeBoosters.includes(boostId)
        ? prev.activeBoosters
        : [...prev.activeBoosters, boostId]
    }));

    const newTx: Transaction = {
      id: 'tx-' + Date.now(),
      timestamp: Date.now(),
      type: 'subscription',
      amount: -cost,
      description: `Activated booster: ${boostId}`
    };
    setTransactions((prev) => [newTx, ...prev]);
    showNotice('Booster activated successfully!');
  };

  const handleQualifyReferral = (refId: string) => {
    setReferrals((prev) =>
      prev.map((r) =>
        r.id === refId ? { ...r, status: 'QUALIFIED', mintEarned: 100 } : r
      )
    );

    // Update user balance and rank
    setUser((prev) => ({
      ...prev,
      walletBalance: prev.walletBalance + 100,
      totalEarned: prev.totalEarned + 100
    }));

    // Update user's position in leaderboard
    setLeaderboard((prev) =>
      prev.map((entry) => {
        if (entry.isCurrentUser) {
          const newQualified = entry.qualifiedCount + 1;
          const newRank = Math.max(1, entry.rank - 3);
          return {
            ...entry,
            qualifiedCount: newQualified,
            previousRank: entry.rank,
            rank: newRank
          };
        }
        return entry;
      })
    );

    const newTx: Transaction = {
      id: 'tx-' + Date.now(),
      timestamp: Date.now(),
      type: 'referral',
      amount: 100,
      description: `Referral Reward for ID ${refId}`
    };
    setTransactions((prev) => [newTx, ...prev]);
  };

  const handleTrustWalletConnect = (network: 'BNB Smart Chain' | 'Ethereum', address: string, balance: number) => {
    setUser((prev) => ({
      ...prev,
      isWalletConnected: true,
      connectedNetwork: network,
      connectedChainId: network === 'BNB Smart Chain' ? 56 : 1,
      walletAddress: address,
      nativeAssetBalance: balance
    }));
    showNotice(`Trust Wallet connected on ${network}.`);
  };

  const handleDisconnectWallet = () => {
    setUser((prev) => ({
      ...prev,
      isWalletConnected: false,
      connectedNetwork: null,
      connectedChainId: null,
      walletAddress: '',
      nativeAssetBalance: 0
    }));
    showNotice('Web3 wallet disconnected.');
  };

  const handleWithdraw = (amount: number, address: string): boolean => {
    setUser((prev) => ({
      ...prev,
      walletBalance: prev.walletBalance - amount
    }));

    const newTx: Transaction = {
      id: 'tx-' + Date.now(),
      timestamp: Date.now(),
      type: 'withdraw',
      amount: -amount,
      description: `Withdrawal to ${address.substring(0, 6)}...${address.substring(address.length - 4)}`,
      network: user.connectedNetwork || 'BNB Smart Chain',
      txHash: '0x' + Math.random().toString(16).substring(2, 12) + '...'
    };
    setTransactions((prev) => [newTx, ...prev]);
    return true;
  };

  const handleDeposit = (amount: number) => {
    setUser((prev) => ({
      ...prev,
      walletBalance: prev.walletBalance + amount
    }));

    const newTx: Transaction = {
      id: 'tx-' + Date.now(),
      timestamp: Date.now(),
      type: 'deposit',
      amount: amount,
      description: 'MINT Test Deposit',
      txHash: '0x' + Math.random().toString(16).substring(2, 12) + '...'
    };
    setTransactions((prev) => [newTx, ...prev]);
  };

  if (!isAuthenticated) {
    return (
      <AuthScreen
        onLoginSuccess={(userData) => {
          setUser((prev) => ({
            ...prev,
            username: userData.username,
            email: userData.email,
            phone: userData.phone,
            monetaId: userData.monetaId
          }));
          setIsAuthenticated(true);
        }}
        onOpenTrustWallet={() => setIsTrustWalletModalOpen(true)}
      />
    );
  }

  return (
    <div className="min-h-screen bg-[#0e1117] text-[#f1f5f9] flex flex-col font-sans">
      {/* Top Bar with Live Balance */}
      <TopBar user={user} isSyncing={isSyncing} onSync={handleSync} />

      {/* Main Container */}
      <main className="app-container flex-1">
        {currentTab === 'miner' && (
          <MinerScreen
            user={user}
            telemetry={telemetry}
            onToggleMining={handleToggleMining}
            onUpgradeMiner={handleUpgradeMiner}
            onShowNotice={showNotice}
          />
        )}

        {currentTab === 'boost' && (
          <BoostScreen
            user={user}
            onActivateBoost={handleActivateBoost}
            onShowNotice={showNotice}
          />
        )}

        {currentTab === 'friends' && (
          <FriendsScreen
            user={user}
            referrals={referrals}
            leaderboard={leaderboard}
            onQualifyReferral={handleQualifyReferral}
            onShowNotice={showNotice}
          />
        )}

        {currentTab === 'wallet' && (
          <WalletScreen
            user={user}
            transactions={transactions}
            onOpenTrustWalletModal={() => setIsTrustWalletModalOpen(true)}
            onDisconnectWallet={handleDisconnectWallet}
            onWithdraw={handleWithdraw}
            onDeposit={handleDeposit}
            onShowNotice={showNotice}
          />
        )}
      </main>

      {/* Bottom Navigation */}
      <BottomNav
        currentTab={currentTab}
        onSelectTab={setCurrentTab}
        friendsNotificationCount={referrals.filter((r) => r.status === 'QUALIFICATION_PENDING').length}
      />

      {/* Trust Wallet Modal */}
      <TrustWalletModal
        isOpen={isTrustWalletModalOpen}
        onClose={() => setIsTrustWalletModalOpen(false)}
        onConnect={handleTrustWalletConnect}
      />

      {/* Toast Notification */}
      {toastMessage && (
        <div className="fixed top-16 left-1/2 transform -translate-x-1/2 z-50 bg-[#151922] border border-[#00e5ff] text-[#f1f5f9] px-4 py-2.5 rounded-xl shadow-2xl text-xs font-semibold flex items-center gap-2 animate-bounce">
          <span className="w-2 h-2 rounded-full bg-[#00e5ff] animate-ping" />
          <span>{toastMessage}</span>
        </div>
      )}
    </div>
  );
};

export default App;
