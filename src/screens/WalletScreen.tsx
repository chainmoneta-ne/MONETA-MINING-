import React, { useState } from 'react';
import { 
  Wallet, 
  ArrowUpRight, 
  ArrowDownLeft, 
  Clock, 
  ExternalLink, 
  ShieldCheck, 
  CheckCircle2, 
  AlertCircle,
  Copy,
  Link2
} from 'lucide-react';
import { UserProfile, Transaction } from '../types';

interface WalletScreenProps {
  user: UserProfile;
  transactions: Transaction[];
  onOpenTrustWalletModal: () => void;
  onDisconnectWallet: () => void;
  onWithdraw: (amount: number, address: string) => boolean;
  onDeposit: (amount: number) => void;
  onShowNotice: (msg: string) => void;
}

export const WalletScreen: React.FC<WalletScreenProps> = ({
  user,
  transactions,
  onOpenTrustWalletModal,
  onDisconnectWallet,
  onWithdraw,
  onDeposit,
  onShowNotice
}) => {
  const [activeAction, setActiveAction] = useState<'none' | 'withdraw' | 'deposit'>('none');
  const [withdrawAmount, setWithdrawAmount] = useState('');
  const [withdrawAddress, setWithdrawAddress] = useState('');
  const [withdrawError, setWithdrawError] = useState<string | null>(null);

  const mintPriceUsd = 0.45;
  const balanceUsd = (user.walletBalance * mintPriceUsd).toFixed(2);

  const handleWithdrawSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setWithdrawError(null);
    const amount = parseFloat(withdrawAmount);

    if (isNaN(amount) || amount <= 0) {
      setWithdrawError('Please enter a valid amount.');
      return;
    }
    if (amount < 20) {
      setWithdrawError('Minimum withdrawal is 20 MINT.');
      return;
    }
    if (amount > user.walletBalance) {
      setWithdrawError('Insufficient balance.');
      return;
    }
    if (!withdrawAddress.startsWith('0x') || withdrawAddress.length < 20) {
      setWithdrawError('Please enter a valid BEP-20 or ERC-20 EVM address (starts with 0x).');
      return;
    }

    const ok = onWithdraw(amount, withdrawAddress);
    if (ok) {
      setWithdrawAmount('');
      setWithdrawAddress('');
      setActiveAction('none');
      onShowNotice(`Withdrawal request submitted for ${amount} MINT.`);
    }
  };

  const handleDepositQuick = (amt: number) => {
    onDeposit(amt);
    onShowNotice(`Credited ${amt} MINT to test balance.`);
    setActiveAction('none');
  };

  return (
    <div className="space-y-4 px-4 pt-4 pb-12">
      {/* Title */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-[#f1f5f9]">WALLET</h1>
          <p className="text-xs text-[#94a3b8]">MINT Ledger & Non-Custodial Web3 Settlement</p>
        </div>
        <div className="badge badge-blue flex items-center gap-1.5">
          <ShieldCheck className="w-3.5 h-3.5" />
          <span>ON-CHAIN AUDITED</span>
        </div>
      </div>

      {/* Main Balance Card */}
      <div className="card bg-gradient-to-br from-[#151922] via-[#1c2230] to-[#212838] border-[#262f42] p-6 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 right-0 w-48 h-48 bg-[#00e5ff]/5 rounded-full blur-3xl pointer-events-none" />
        
        <span className="text-xs font-bold uppercase tracking-wider text-[#94a3b8] block">
          TOTAL MINT BALANCE
        </span>
        <div className="text-3xl font-mono font-bold text-gradient-moneta mt-1 mb-0.5">
          {user.walletBalance.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} MINT
        </div>
        <div className="text-xs font-mono text-[#94a3b8]">
          ≈ ${balanceUsd} USD (@ ${mintPriceUsd}/MINT)
        </div>

        {/* Action Buttons */}
        <div className="grid grid-cols-2 gap-3 mt-6">
          <button
            onClick={() => setActiveAction(activeAction === 'deposit' ? 'none' : 'deposit')}
            className="btn-secondary py-3 flex items-center justify-center gap-2"
          >
            <ArrowDownLeft className="w-4 h-4 text-[#00e676]" />
            <span>DEPOSIT</span>
          </button>

          <button
            onClick={() => setActiveAction(activeAction === 'withdraw' ? 'none' : 'withdraw')}
            className="btn-primary py-3 flex items-center justify-center gap-2"
          >
            <ArrowUpRight className="w-4 h-4" />
            <span>WITHDRAW</span>
          </button>
        </div>
      </div>

      {/* DEPOSIT ACTION DRAWER */}
      {activeAction === 'deposit' && (
        <div className="card bg-[#151922] border-[#00e676]/40 p-5 animate-fadeIn">
          <h3 className="text-sm font-bold text-[#f1f5f9] mb-1">Deposit MINT or Assets</h3>
          <p className="text-xs text-[#94a3b8] mb-4">
            Deposit from external Web3 wallet or request test allocation.
          </p>
          <div className="grid grid-cols-3 gap-2">
            <button onClick={() => handleDepositQuick(50)} className="btn-secondary py-2 text-xs">
              +50 MINT
            </button>
            <button onClick={() => handleDepositQuick(100)} className="btn-secondary py-2 text-xs">
              +100 MINT
            </button>
            <button onClick={() => handleDepositQuick(250)} className="btn-secondary py-2 text-xs">
              +250 MINT
            </button>
          </div>
        </div>
      )}

      {/* WITHDRAW ACTION DRAWER */}
      {activeAction === 'withdraw' && (
        <form onSubmit={handleWithdrawSubmit} className="card bg-[#151922] border-[#00e5ff]/40 p-5 space-y-3 animate-fadeIn">
          <h3 className="text-sm font-bold text-[#f1f5f9]">Withdraw MINT to External Address</h3>
          
          <div>
            <label className="text-[11px] font-bold text-[#94a3b8] uppercase block mb-1">
              Destination EVM Address
            </label>
            <input
              type="text"
              placeholder="0x..."
              value={withdrawAddress}
              onChange={(e) => setWithdrawAddress(e.target.value)}
              className="w-full bg-[#1c2230] border border-[#262f42] rounded-xl px-3 py-2.5 text-xs font-mono text-[#f1f5f9] outline-none focus:border-[#00e5ff]"
            />
          </div>

          <div>
            <div className="flex justify-between text-[11px] font-bold text-[#94a3b8] uppercase mb-1">
              <span>Amount (MINT)</span>
              <span>Available: {user.walletBalance.toFixed(2)}</span>
            </div>
            <input
              type="number"
              step="any"
              placeholder="20.00"
              value={withdrawAmount}
              onChange={(e) => setWithdrawAmount(e.target.value)}
              className="w-full bg-[#1c2230] border border-[#262f42] rounded-xl px-3 py-2.5 text-xs font-mono text-[#f1f5f9] outline-none focus:border-[#00e5ff]"
            />
          </div>

          {withdrawError && (
            <div className="flex items-center gap-1.5 text-xs text-[#ff3d71] bg-[#ff3d71]/10 p-2.5 rounded-xl border border-[#ff3d71]/20">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{withdrawError}</span>
            </div>
          )}

          <div className="flex gap-2 pt-2">
            <button
              type="button"
              onClick={() => setActiveAction('none')}
              className="btn-secondary flex-1 py-2 text-xs"
            >
              Cancel
            </button>
            <button type="submit" className="btn-primary flex-1 py-2 text-xs">
              Confirm Withdrawal
            </button>
          </div>
        </form>
      )}

      {/* WEB3 SETTLEMENT WALLET SECTION */}
      <div className="card bg-[#151922] border-[#262f42] p-5">
        <div className="flex items-center justify-between mb-3 pb-3 border-b border-[#262f42]">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-[#00e5ff]/10 border border-[#00e5ff]/30 text-[#00e5ff] flex items-center justify-center">
              <Link2 className="w-4 h-4" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-[#f1f5f9]">Trust Wallet Web3 Layer</h3>
              <p className="text-xs text-[#94a3b8]">EVM Smart Contract Integration</p>
            </div>
          </div>
          <span className={`badge ${user.isWalletConnected ? 'badge-green' : 'badge-gray'}`}>
            {user.isWalletConnected ? 'CONNECTED' : 'DISCONNECTED'}
          </span>
        </div>

        {user.isWalletConnected ? (
          <div className="space-y-3">
            <div className="bg-[#1c2230] p-3 rounded-xl border border-[#262f42] flex items-center justify-between">
              <div>
                <span className="text-[10px] text-[#94a3b8] uppercase font-bold block">Network</span>
                <span className="text-xs font-bold text-[#f1f5f9]">{user.connectedNetwork}</span>
              </div>
              <div className="text-right">
                <span className="text-[10px] text-[#94a3b8] uppercase font-bold block">Native Balance</span>
                <span className="text-xs font-mono font-bold text-[#00e676]">
                  {user.nativeAssetBalance} {user.connectedNetwork === 'Ethereum' ? 'ETH' : 'BNB'}
                </span>
              </div>
            </div>

            <div className="bg-[#1c2230] p-3 rounded-xl border border-[#262f42]">
              <span className="text-[10px] text-[#94a3b8] uppercase font-bold block">Connected Address</span>
              <span className="text-xs font-mono text-[#00e5ff] break-all">{user.walletAddress}</span>
            </div>

            <button
              onClick={onDisconnectWallet}
              className="btn-outline w-full py-2 text-xs justify-center border-[#ff3d71]/40 text-[#ff3d71] hover:bg-[#ff3d71]/10"
            >
              Disconnect Web3 Wallet
            </button>
          </div>
        ) : (
          <div className="text-center py-2">
            <p className="text-xs text-[#94a3b8] mb-3">
              Connect Trust Wallet or any EVM wallet to enable instant on-chain payouts on BNB Smart Chain.
            </p>
            <button
              onClick={onOpenTrustWalletModal}
              className="btn-primary w-full py-2.5 text-xs"
            >
              <Wallet className="w-4 h-4" />
              Connect Trust Wallet
            </button>
          </div>
        )}
      </div>

      {/* TRANSACTION HISTORY LEDGER */}
      <div className="card bg-[#151922] border-[#262f42] p-5">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xs font-bold uppercase tracking-wider text-[#94a3b8]">
            TRANSACTION LEDGER
          </h2>
          <span className="text-xs font-mono text-[#94a3b8]">
            {transactions.length} Records
          </span>
        </div>

        <div className="space-y-2">
          {transactions.map((tx) => (
            <div
              key={tx.id}
              className="p-3 bg-[#1c2230] rounded-xl border border-[#262f42] flex items-center justify-between"
            >
              <div>
                <div className="text-xs font-semibold text-[#f1f5f9]">{tx.description}</div>
                <div className="text-[10px] font-mono text-[#94a3b8]">
                  {new Date(tx.timestamp).toLocaleString()} • {tx.txHash || 'Consensus Internal'}
                </div>
              </div>

              <div className="text-right">
                <span
                  className={`text-xs font-mono font-bold ${
                    tx.amount >= 0 ? 'text-[#00e676]' : 'text-[#ff3d71]'
                  }`}
                >
                  {tx.amount >= 0 ? `+${tx.amount.toFixed(2)}` : tx.amount.toFixed(2)} MINT
                </span>
                <span className="text-[9px] uppercase tracking-wider text-[#94a3b8] block">
                  {tx.type}
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
