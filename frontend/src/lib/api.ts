const BASE_URL = "http://localhost:8080/api/v1";

export interface BedrockModel {
  id: string;
  displayName: string;
  modelId: string;
  provider: string;
}

export type GroupedModels = Record<string, BedrockModel[]>;

export async function fetchBedrockModels(): Promise<GroupedModels> {
  const res = await fetch(`${BASE_URL}/bedrockModels`);
  if (!res.ok) throw new Error("Failed to fetch Bedrock models");
  return res.json();
}

export async function sendPrompt(
  prompt: string,
  model: string,
  bedrockModel?: string,
  conversationId?: string
): Promise<string> {
  const params = new URLSearchParams({ prompt, model });
  if (model === "bedrock" && bedrockModel) {
    params.set("bedrockModel", bedrockModel);
  }
  if (conversationId) {
    params.set("conversationId", conversationId);
  }
  const res = await fetch(`${BASE_URL}/stormData?${params.toString()}`);
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || "Request failed");
  }
  return res.text();
}
