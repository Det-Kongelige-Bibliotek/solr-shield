/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package dk.kb.solrshield;

import dk.kb.solrshield.rule.Rule;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

/**
 * A collection of {@link Rule}s for an argument (i.e. HTTP GET argument).
 */
public class Argument<T> {
    // TODO: Fill from YAML
    private final String key;
    private Cost baseCost = Cost.NEUTRAL;
    private List<Rule<T>> rules;

    private Function<String, T> valueParser;

    public Argument(String key) {
        this.key = key;
    }

    public Cost calculateCost(String value) {
        final T typedValue = valueParser.apply(value);
        if (typedValue == null) {
            return new Cost(String.format(Locale.ROOT, "Error: Unable to parse value '%s' for key '%s'",
                                          value, key));
        }
        // Iterate the rules and return the first that returns non-null for calculateCost
        return rules.stream().
                map(rule -> rule.calculateCost(typedValue)).
                filter(Objects::nonNull).
                findFirst().
                orElse(baseCost);
    }
}
