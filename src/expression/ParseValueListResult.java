package expression;

import java.util.List;

public record ParseValueListResult(List<Value> values, int tokensConsumed) {
}
