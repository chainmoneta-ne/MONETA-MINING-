import React from 'react';

interface OrigamiCubeLoaderProps {
  size?: number; // size in px, defaults to 70
  text?: string | null;
  fullScreen?: boolean;
}

export const OrigamiCubeLoader: React.FC<OrigamiCubeLoaderProps> = ({
  size = 70,
  text = 'Connecting to MONETA CHAIN...',
  fullScreen = false
}) => {
  // Isometric cube geometry:
  // cx = size / 2, cy = size / 2, r = size * 0.45
  // angle30 = 30 deg => dx = r * cos(30), dy = r * sin(30)
  const cx = size / 2;
  const cy = size / 2;
  const r = (size / 2) * 0.88;
  const dx = r * Math.cos(Math.PI / 6);
  const dy = r * Math.sin(Math.PI / 6);

  const pTop = `${cx},${cy - r}`;
  const pBottom = `${cx},${cy + r}`;
  const pTopLeft = `${cx - dx},${cy - dy}`;
  const pBottomLeft = `${cx - dx},${cy + dy}`;
  const pTopRight = `${cx + dx},${cy - dy}`;
  const pBottomRight = `${cx + dx},${cy + dy}`;
  const pCenter = `${cx},${cy}`;

  const content = (
    <div className="flex flex-col items-center justify-center p-4">
      <div
        className="relative flex items-center justify-center animate-pulse-glow"
        style={{
          width: size,
          height: size
        }}
      >
        <svg
          width={size}
          height={size}
          viewBox={`0 0 ${size} ${size}`}
          className="animate-spin-slow drop-shadow-[0_0_12px_rgba(0,229,255,0.4)]"
          style={{ animationDuration: '6s' }}
        >
          {/* Top Face - Electric Blue #00E5FF */}
          <polygon
            points={`${pTopLeft} ${pTop} ${pTopRight} ${pCenter}`}
            fill="#00E5FF"
            stroke="rgba(255,255,255,0.45)"
            strokeWidth="1.5"
            strokeLinejoin="round"
          />
          {/* Left Face - Royal Purple #7B1FA2 */}
          <polygon
            points={`${pTopLeft} ${pCenter} ${pBottom} ${pBottomLeft}`}
            fill="#7B1FA2"
            stroke="rgba(255,255,255,0.45)"
            strokeWidth="1.5"
            strokeLinejoin="round"
          />
          {/* Right Face - Emerald Green #00C853 */}
          <polygon
            points={`${pCenter} ${pTopRight} ${pBottomRight} ${pBottom}`}
            fill="#00C853"
            stroke="rgba(255,255,255,0.45)"
            strokeWidth="1.5"
            strokeLinejoin="round"
          />
        </svg>
      </div>

      {text && (
        <p className="mt-4 text-sm font-semibold tracking-wider text-white text-center animate-pulse">
          {text}
        </p>
      )}
    </div>
  );

  if (fullScreen) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-[#050505]/90 backdrop-blur-md">
        {content}
      </div>
    );
  }

  return content;
};
