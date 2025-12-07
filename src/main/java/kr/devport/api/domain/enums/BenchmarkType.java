package kr.devport.api.domain.enums;

/**
 * LLM Benchmark Types - 18 categories total
 * Source: Artificial Analysis API
 */
public enum BenchmarkType {

    // Agentic Capabilities (2)
    TERMINAL_BENCH_HARD,    // Agentic Coding & Terminal Use
    TAU_BENCH_TELECOM,      // Agentic Tool Use

    // Reasoning & Knowledge (4)
    AA_LCR,                 // Long Context Reasoning
    HUMANITYS_LAST_EXAM,    // Reasoning & Knowledge
    MMLU_PRO,               // Reasoning & Knowledge (Advanced)
    GPQA_DIAMOND,           // Scientific Reasoning

    // Coding (2)
    LIVECODE_BENCH,         // Coding (Real-world problems)
    SCICODE,                // Scientific Computing & Code

    // Specialized Skills (6)
    IFBENCH,                // Instruction Following
    MATH_500,               // Math 500
    AIME,                   // AIME (Legacy version)
    AIME_2025,              // AIME 2025 (Competition Math)
    CRIT_PT,                // Physics Reasoning
    MMMU_PRO,               // Visual Reasoning (Multimodal)

    // Composite Indices (4)
    AA_INTELLIGENCE_INDEX,  // Overall Intelligence Score
    AA_OMNISCIENCE_INDEX,   // Omniscience Index (breadth)
    AA_CODING_INDEX,        // Coding Index (composite)
    AA_MATH_INDEX           // Math Index (composite)
}
