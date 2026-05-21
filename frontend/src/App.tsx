import { useEffect, useRef, useState } from "react";
import { CloudLightning, Loader2, Zap, Shield, Sparkles } from "lucide-react";
import ChatMessage from "./components/ChatMessage";
import ChatInput from "./components/ChatInput";
import ModelSelector from "./components/ModelSelector";
import { sendPrompt } from "./lib/api";

interface Message {
  role: "user" | "bot";
  content: string;
}

function App() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [loading, setLoading] = useState(false);
  const [model, setModel] = useState("gemini");
  const [bedrockModel, setBedrockModel] = useState("");
  const [conversationId] = useState(() => {
    const stored = localStorage.getItem("stormguard_conversation_id");
    if (stored) return stored;
    const newId = crypto.randomUUID();
    localStorage.setItem("stormguard_conversation_id", newId);
    return newId;
  });
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, loading]);

  const handleSend = async (text: string) => {
    setMessages((prev) => [...prev, { role: "user", content: text }]);
    setLoading(true);
    try {
      const response = await sendPrompt(text, model, bedrockModel || undefined, conversationId);
      setMessages((prev) => [...prev, { role: "bot", content: response }]);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Something went wrong";
      setMessages((prev) => [
        ...prev,
        { role: "bot", content: `Error: ${msg}` },
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", height: "100vh", background: "#f0f2f5" }}>

      {/* Header */}
      <header style={{ background: "#ffffff", borderBottom: "1px solid #e2e8f0", padding: "12px 24px", display: "flex", alignItems: "center", justifyContent: "space-between", boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
        <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
          <div style={{ width: 40, height: 40, borderRadius: 12, background: "linear-gradient(135deg, #f59e0b, #ef4444)", display: "flex", alignItems: "center", justifyContent: "center", boxShadow: "0 4px 12px rgba(245,158,11,0.3)" }}>
            <CloudLightning style={{ width: 22, height: 22, color: "white" }} />
          </div>
          <div>
            <h1 style={{ fontSize: 18, fontWeight: 700, color: "#1e293b", margin: 0 }}>StormGuard AI</h1>
            <p style={{ fontSize: 11, color: "#94a3b8", margin: 0, letterSpacing: 1, textTransform: "uppercase" }}>Weather Intelligence</p>
          </div>
        </div>
        <ModelSelector
          model={model}
          setModel={setModel}
          bedrockModel={bedrockModel}
          setBedrockModel={setBedrockModel}
        />
      </header>

      {/* Chat area */}
      <main style={{ flex: 1, overflowY: "auto", padding: "24px 16px" }} className="scrollbar-thin">
        <div style={{ maxWidth: 720, margin: "0 auto" }}>
          {messages.length === 0 && (
            <div style={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", minHeight: "55vh", gap: 24 }}>
              <div className="animate-float">
                <div style={{ width: 72, height: 72, borderRadius: 20, background: "linear-gradient(135deg, rgba(245,158,11,0.15), rgba(239,68,68,0.1))", border: "2px solid rgba(245,158,11,0.2)", display: "flex", alignItems: "center", justifyContent: "center" }}>
                  <CloudLightning style={{ width: 36, height: 36, color: "#f59e0b" }} />
                </div>
              </div>
              <div style={{ textAlign: "center" }}>
                <h2 style={{ fontSize: 24, fontWeight: 700, color: "#1e293b", marginBottom: 8 }}>What would you like to know?</h2>
                <p style={{ fontSize: 14, color: "#94a3b8" }}>Ask about weather alerts, storm tracking, forecasts, or anything else.</p>
              </div>

              {/* Quick action cards */}
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: 12, width: "100%", maxWidth: 480, marginTop: 8 }}>
                <button
                  onClick={() => handleSend("Are there any active storm alerts in California?")}
                  style={{ background: "white", border: "1px solid #e2e8f0", borderRadius: 12, padding: 16, textAlign: "left", cursor: "pointer", transition: "all 0.2s" }}
                  onMouseEnter={e => { e.currentTarget.style.borderColor = "#f59e0b"; e.currentTarget.style.boxShadow = "0 4px 12px rgba(245,158,11,0.12)"; }}
                  onMouseLeave={e => { e.currentTarget.style.borderColor = "#e2e8f0"; e.currentTarget.style.boxShadow = "none"; }}
                >
                  <Zap style={{ width: 20, height: 20, color: "#f59e0b", marginBottom: 8 }} />
                  <p style={{ fontSize: 12, color: "#64748b", lineHeight: 1.4, margin: 0 }}>Storm alerts in California</p>
                </button>
                <button
                  onClick={() => handleSend("What's the weather forecast for New York?")}
                  style={{ background: "white", border: "1px solid #e2e8f0", borderRadius: 12, padding: 16, textAlign: "left", cursor: "pointer", transition: "all 0.2s" }}
                  onMouseEnter={e => { e.currentTarget.style.borderColor = "#3b82f6"; e.currentTarget.style.boxShadow = "0 4px 12px rgba(59,130,246,0.12)"; }}
                  onMouseLeave={e => { e.currentTarget.style.borderColor = "#e2e8f0"; e.currentTarget.style.boxShadow = "none"; }}
                >
                  <Shield style={{ width: 20, height: 20, color: "#3b82f6", marginBottom: 8 }} />
                  <p style={{ fontSize: 12, color: "#64748b", lineHeight: 1.4, margin: 0 }}>Weather in New York</p>
                </button>
                <button
                  onClick={() => handleSend("Tell me a fun fact about hurricanes")}
                  style={{ background: "white", border: "1px solid #e2e8f0", borderRadius: 12, padding: 16, textAlign: "left", cursor: "pointer", transition: "all 0.2s" }}
                  onMouseEnter={e => { e.currentTarget.style.borderColor = "#8b5cf6"; e.currentTarget.style.boxShadow = "0 4px 12px rgba(139,92,246,0.12)"; }}
                  onMouseLeave={e => { e.currentTarget.style.borderColor = "#e2e8f0"; e.currentTarget.style.boxShadow = "none"; }}
                >
                  <Sparkles style={{ width: 20, height: 20, color: "#8b5cf6", marginBottom: 8 }} />
                  <p style={{ fontSize: 12, color: "#64748b", lineHeight: 1.4, margin: 0 }}>Fun facts about hurricanes</p>
                </button>
              </div>

              <p style={{ fontSize: 11, color: "#94a3b8", marginTop: 8 }}>
                Powered by <span style={{ color: "#475569", fontWeight: 600 }}>{model.charAt(0).toUpperCase() + model.slice(1)}</span>
              </p>
            </div>
          )}

          <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
            {messages.map((msg, i) => (
              <ChatMessage key={i} role={msg.role} content={msg.content} />
            ))}
          </div>

          {loading && (
            <div style={{ display: "flex", alignItems: "center", gap: 12, padding: "12px 0", marginTop: 16 }}>
              <div style={{ width: 32, height: 32, borderRadius: "50%", background: "#ecfdf5", border: "1px solid #a7f3d0", display: "flex", alignItems: "center", justifyContent: "center" }}>
                <Loader2 style={{ width: 16, height: 16, color: "#10b981", animation: "spin 1s linear infinite" }} />
              </div>
              <div style={{ display: "flex", gap: 4 }}>
                <span style={{ width: 8, height: 8, borderRadius: "50%", background: "#94a3b8", animation: "bounce 1.4s infinite", animationDelay: "0ms" }} />
                <span style={{ width: 8, height: 8, borderRadius: "50%", background: "#94a3b8", animation: "bounce 1.4s infinite", animationDelay: "200ms" }} />
                <span style={{ width: 8, height: 8, borderRadius: "50%", background: "#94a3b8", animation: "bounce 1.4s infinite", animationDelay: "400ms" }} />
              </div>
            </div>
          )}

          <div ref={bottomRef} />
        </div>
      </main>

      {/* Input */}
      <footer style={{ background: "white", borderTop: "1px solid #e2e8f0", padding: "16px", boxShadow: "0 -1px 3px rgba(0,0,0,0.05)" }}>
        <div style={{ maxWidth: 720, margin: "0 auto" }}>
          <ChatInput onSend={handleSend} disabled={loading} />
        </div>
      </footer>
    </div>
  );
}

export default App;
