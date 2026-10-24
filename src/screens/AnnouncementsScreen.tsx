import React from 'react';
import { Bell, ArrowLeft, Calendar, ShieldCheck, Sparkles, Cpu, Wrench } from 'lucide-react';

interface AnnouncementsScreenProps {
  onBack: () => void;
}

export const AnnouncementsScreen: React.FC<AnnouncementsScreenProps> = ({ onBack }) => {
  const announcements = [
    {
      id: '1',
      title: 'Mining Updates',
      desc: "We've increased the base mining rate for this week!",
      tag: 'HOT',
      tagColor: 'badge-gold',
      icon: Sparkles,
      date: '2 hours ago'
    },
    {
      id: '2',
      title: 'Maintenance',
      desc: 'Scheduled maintenance on Sunday 2AM UTC.',
      tag: 'SYSTEM',
      tagColor: 'badge-blue',
      icon: Wrench,
      date: 'Yesterday'
    },
    {
      id: '3',
      title: 'New Features',
      desc: 'Digital Cards are now available.',
      tag: 'NEW',
      tagColor: 'badge-green',
      icon: Cpu,
      date: '3 days ago'
    },
    {
      id: '4',
      title: 'Security Notice',
      desc: 'Please enable 2FA to protect your account.',
      tag: 'SECURITY',
      tagColor: 'badge-red',
      icon: ShieldCheck,
      date: 'May 12'
    },
    {
      id: '5',
      title: 'MONETA Chain',
      desc: 'Testnet phase 2 is launching next month.',
      tag: 'NETWORK',
      tagColor: 'badge-blue',
      icon: Bell,
      date: 'May 08'
    }
  ];

  return (
    <div className="space-y-4 px-4 pt-4 pb-12">
      {/* Top Header */}
      <div className="flex items-center gap-3">
        <button
          onClick={onBack}
          className="w-9 h-9 rounded-xl bg-[#1c2230] border border-[#262f42] flex items-center justify-center text-[#94a3b8] hover:text-[#00e5ff] transition-all"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <div>
          <h1 className="text-xl font-bold tracking-tight text-gradient-moneta">
            MONETA MINING ANNOUNCEMENTS
          </h1>
          <p className="text-xs text-[#94a3b8]">Official network dispatches & protocol releases</p>
        </div>
      </div>

      {/* Announcements List */}
      <div className="space-y-3 pt-2">
        {announcements.map((item) => {
          const Icon = item.icon;
          return (
            <div
              key={item.id}
              className="card bg-[#151922] border-[#262f42] hover:border-[#00e5ff]/40 p-5 transition-all"
            >
              <div className="flex items-start justify-between gap-3 mb-2">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-[#1c2230] border border-[#262f42] flex items-center justify-center text-[#00e5ff]">
                    <Icon className="w-5 h-5" />
                  </div>
                  <div>
                    <h3 className="text-sm font-bold text-[#00e5ff]">{item.title}</h3>
                    <span className="text-[10px] text-[#94a3b8] flex items-center gap-1 mt-0.5">
                      <Calendar className="w-3 h-3" />
                      {item.date}
                    </span>
                  </div>
                </div>
                <span className={`badge ${item.tagColor} text-[10px]`}>{item.tag}</span>
              </div>
              <p className="text-xs text-[#f1f5f9] mt-2 leading-relaxed pl-13">
                {item.desc}
              </p>
            </div>
          );
        })}
      </div>
    </div>
  );
};
