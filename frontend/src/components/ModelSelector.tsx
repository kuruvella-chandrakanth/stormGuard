import { useEffect, useState } from "react";
import { ChevronDown } from "lucide-react";
import { fetchBedrockModels, type GroupedModels } from "../lib/api";

interface Props {
  model: string;
  setModel: (m: string) => void;
  bedrockModel: string;
  setBedrockModel: (m: string) => void;
}

const MODEL_CONFIG: Record<string, { label: string; bg: string; color: string; dot: string }> = {
  gemini:  { label: "Gemini",  bg: "#eff6ff", color: "#2563eb", dot: "#3b82f6" },
  groq:    { label: "Groq",    bg: "#fff7ed", color: "#ea580c", dot: "#f97316" },
  bedrock: { label: "Bedrock", bg: "#ecfdf5", color: "#059669", dot: "#10b981" },
};

const PROVIDER_COLORS: Record<string, string> = {
  Anthropic: "#7c3aed",
  Meta:      "#2563eb",
  DeepSeek:  "#0891b2",
  Amazon:    "#ea580c",
  Mistral:   "#dc2626",
};

export default function ModelSelector({
  model,
  setModel,
  bedrockModel,
  setBedrockModel,
}: Props) {
  const [grouped, setGrouped] = useState<GroupedModels>({});
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [bedrockOpen, setBedrockOpen] = useState(false);

  useEffect(() => {
    if (model === "bedrock" && Object.keys(grouped).length === 0) {
      setLoading(true);
      fetchBedrockModels()
        .then((data) => {
          setGrouped(data);
          const firstProvider = Object.keys(data)[0];
          if (firstProvider && data[firstProvider].length > 0 && !bedrockModel) {
            setBedrockModel(data[firstProvider][0].modelId);
          }
        })
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [model]);

  const cfg = MODEL_CONFIG[model] || MODEL_CONFIG.gemini;

  const getBedrockDisplayName = () => {
    for (const models of Object.values(grouped)) {
      const found = models.find((m) => m.modelId === bedrockModel);
      if (found) return found.displayName;
    }
    return "Select model";
  };

  return (
    <div style={{ display: "flex", alignItems: "center", gap: 10, flexWrap: "wrap" }}>
      {/* Main model selector */}
      <div style={{ position: "relative", zIndex: 50 }}>
        <button
          onClick={() => { setOpen(!open); setBedrockOpen(false); }}
          style={{ display: "flex", alignItems: "center", gap: 8, padding: "8px 14px", borderRadius: 10, border: `2px solid ${cfg.dot}40`, background: cfg.bg, color: cfg.color, fontSize: 13, fontWeight: 600, cursor: "pointer" }}
        >
          <span style={{ width: 8, height: 8, borderRadius: "50%", background: cfg.dot }} />
          {cfg.label}
          <ChevronDown style={{ width: 14, height: 14, transform: open ? "rotate(180deg)" : "none", transition: "transform 0.2s" }} />
        </button>

        {open && (
          <div style={{ position: "absolute", right: 0, top: "100%", marginTop: 8, background: "white", border: "1px solid #e2e8f0", borderRadius: 12, boxShadow: "0 8px 30px rgba(0,0,0,0.12)", overflow: "hidden", minWidth: 170, zIndex: 50 }}>
            {Object.entries(MODEL_CONFIG).map(([key, c]) => (
              <button
                key={key}
                onClick={() => { setModel(key); setOpen(false); }}
                style={{
                  width: "100%",
                  display: "flex",
                  alignItems: "center",
                  gap: 10,
                  padding: "10px 14px",
                  fontSize: 13,
                  border: "none",
                  cursor: "pointer",
                  background: model === key ? c.bg : "white",
                  color: model === key ? c.color : "#64748b",
                  fontWeight: model === key ? 600 : 400,
                  transition: "all 0.15s",
                }}
                onMouseEnter={e => { if (model !== key) e.currentTarget.style.background = "#f8fafc"; }}
                onMouseLeave={e => { if (model !== key) e.currentTarget.style.background = "white"; }}
              >
                <span style={{ width: 9, height: 9, borderRadius: "50%", background: c.dot }} />
                {c.label}
                {model === key && <span style={{ marginLeft: "auto", fontSize: 10, opacity: 0.5 }}>active</span>}
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Bedrock sub-model selector */}
      {model === "bedrock" && (
        <div style={{ position: "relative", zIndex: 50 }}>
          {loading ? (
            <span style={{ color: "#94a3b8", fontSize: 13 }}>Loading models...</span>
          ) : (
            <>
              <button
                onClick={() => { setBedrockOpen(!bedrockOpen); setOpen(false); }}
                style={{ display: "flex", alignItems: "center", gap: 8, padding: "8px 14px", borderRadius: 10, border: "1px solid #e2e8f0", background: "white", color: "#475569", fontSize: 13, fontWeight: 500, cursor: "pointer" }}
              >
                {getBedrockDisplayName()}
                <ChevronDown style={{ width: 14, height: 14, transform: bedrockOpen ? "rotate(180deg)" : "none", transition: "transform 0.2s" }} />
              </button>

              {bedrockOpen && (
                <div style={{ position: "absolute", right: 0, top: "100%", marginTop: 8, background: "white", border: "1px solid #e2e8f0", borderRadius: 12, boxShadow: "0 8px 30px rgba(0,0,0,0.12)", overflow: "hidden", maxHeight: 320, overflowY: "auto", minWidth: 250, zIndex: 50 }} className="scrollbar-thin">
                  {Object.entries(grouped).map(([provider, models]) => (
                    <div key={provider}>
                      <div style={{ padding: "8px 14px", fontSize: 10, fontWeight: 700, textTransform: "uppercase", letterSpacing: 1.5, color: PROVIDER_COLORS[provider] || "#94a3b8", background: "#f8fafc", borderBottom: "1px solid #f1f5f9", position: "sticky", top: 0 }}>
                        {provider}
                      </div>
                      {models.map((m) => (
                        <button
                          key={m.modelId}
                          onClick={() => { setBedrockModel(m.modelId); setBedrockOpen(false); }}
                          style={{
                            width: "100%",
                            textAlign: "left",
                            padding: "9px 14px",
                            fontSize: 13,
                            border: "none",
                            borderLeft: bedrockModel === m.modelId ? `3px solid ${PROVIDER_COLORS[provider] || "#94a3b8"}` : "3px solid transparent",
                            cursor: "pointer",
                            background: bedrockModel === m.modelId ? "#f0fdf4" : "white",
                            color: bedrockModel === m.modelId ? "#059669" : "#475569",
                            fontWeight: bedrockModel === m.modelId ? 600 : 400,
                            transition: "all 0.15s",
                          }}
                          onMouseEnter={e => { if (bedrockModel !== m.modelId) e.currentTarget.style.background = "#f8fafc"; }}
                          onMouseLeave={e => { if (bedrockModel !== m.modelId) e.currentTarget.style.background = "white"; }}
                        >
                          {m.displayName}
                        </button>
                      ))}
                    </div>
                  ))}
                </div>
              )}
            </>
          )}
        </div>
      )}

      {/* Click outside to close */}
      {(open || bedrockOpen) && (
        <div style={{ position: "fixed", inset: 0, zIndex: 40 }} onClick={() => { setOpen(false); setBedrockOpen(false); }} />
      )}
    </div>
  );
}
