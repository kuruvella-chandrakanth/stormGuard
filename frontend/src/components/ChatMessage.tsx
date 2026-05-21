import { Bot, User } from "lucide-react";

interface Props {
  role: "user" | "bot";
  content: string;
}

export default function ChatMessage({ role, content }: Props) {
  const isUser = role === "user";

  return (
    <div style={{ display: "flex", gap: 12, flexDirection: isUser ? "row-reverse" : "row" }}>
      <div
        style={{
          flexShrink: 0,
          width: 36,
          height: 36,
          borderRadius: 10,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          ...(isUser
            ? { background: "linear-gradient(135deg, #3b82f6, #6366f1)", boxShadow: "0 4px 12px rgba(59,130,246,0.25)" }
            : { background: "#ecfdf5", border: "1px solid #a7f3d0" }
          ),
        }}
      >
        {isUser ? (
          <User style={{ width: 16, height: 16, color: "white" }} />
        ) : (
          <Bot style={{ width: 16, height: 16, color: "#10b981" }} />
        )}
      </div>
      <div
        style={{
          maxWidth: "75%",
          borderRadius: 16,
          padding: "12px 16px",
          fontSize: 14,
          lineHeight: 1.6,
          whiteSpace: "pre-wrap",
          ...(isUser
            ? {
                background: "linear-gradient(135deg, #3b82f6, #6366f1)",
                color: "white",
                borderTopRightRadius: 4,
                boxShadow: "0 2px 8px rgba(59,130,246,0.2)",
              }
            : {
                background: "white",
                color: "#334155",
                border: "1px solid #e2e8f0",
                borderTopLeftRadius: 4,
                boxShadow: "0 1px 3px rgba(0,0,0,0.05)",
              }
          ),
        }}
      >
        {content}
      </div>
    </div>
  );
}
