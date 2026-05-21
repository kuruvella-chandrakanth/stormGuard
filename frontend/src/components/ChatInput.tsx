import { useState } from "react";
import { ArrowUp } from "lucide-react";

interface Props {
  onSend: (message: string) => void;
  disabled: boolean;
}

export default function ChatInput({ onSend, disabled }: Props) {
  const [input, setInput] = useState("");
  const [focused, setFocused] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const trimmed = input.trim();
    if (!trimmed || disabled) return;
    onSend(trimmed);
    setInput("");
  };

  const active = input.trim().length > 0 && !disabled;

  return (
    <form onSubmit={handleSubmit} style={{ position: "relative", display: "flex", alignItems: "center" }}>
      <input
        type="text"
        value={input}
        onChange={(e) => setInput(e.target.value)}
        onFocus={() => setFocused(true)}
        onBlur={() => setFocused(false)}
        placeholder="Ask about weather, storms, alerts..."
        disabled={disabled}
        style={{
          width: "100%",
          background: "#f8fafc",
          border: focused ? "2px solid #3b82f6" : "2px solid #e2e8f0",
          borderRadius: 16,
          padding: "14px 56px 14px 20px",
          fontSize: 14,
          color: "#1e293b",
          outline: "none",
          transition: "border-color 0.2s",
          opacity: disabled ? 0.5 : 1,
        }}
      />
      <button
        type="submit"
        disabled={!active}
        style={{
          position: "absolute",
          right: 8,
          width: 40,
          height: 40,
          borderRadius: 12,
          border: "none",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          cursor: active ? "pointer" : "not-allowed",
          transition: "all 0.2s",
          ...(active
            ? { background: "linear-gradient(135deg, #3b82f6, #6366f1)", color: "white", boxShadow: "0 4px 12px rgba(59,130,246,0.3)" }
            : { background: "#e2e8f0", color: "#94a3b8" }
          ),
        }}
      >
        <ArrowUp style={{ width: 18, height: 18 }} />
      </button>
    </form>
  );
}
