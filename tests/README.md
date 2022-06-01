# Test sentences

Consider the knowledge base `ontos/fact.owl`. Below is a list of sentences for test the different features of the
program.

| #   | Text                                                    | Decision | Comment                               |
|-----|---------------------------------------------------------|----------|---------------------------------------|
| 1   | Viruses are not bacteria.                               | TRUE     | Type Negation Pattern - plural        |
| 2   | A virus is not a bacterium.                             | TRUE     | Type Negation Pattern - singular      |
| 3   | Bacteria are viruses.                                   | FALSE    | Positive form                         |
| 4   | Antibiotics kill viruses.                               | FALSE    | VerbNet Role Pattern: Agent, Patient  |
| 5   | Bacitracin kills viruses.                               | FALSE    | 4 + WordNet                           |
| 6   | Amoxicillin kills retrovirus.                           | FALSE    | 4 + WordNet                           |
| 7   | Amoxicillin kills retrovirus. Bacteria are not viruses. | FALSE    | Composed: 1 + 6, with entailed axioms |
| 8   | Covid-19 is caused by SARS-CoV-2.                       | TRUE     | VerbNet Role Pattern: Theme1, Theme2  |
| 9   | Covid-19 is caused by a coronavirus.                    | TRUE     | 8 + concept matching                  |
| 10  | Covid-19 is caused by a bacterium.                      | FALSE    ||
| 11  | SARS-Cov-2 is a virus and a bacterium.                  | FALSE    ||
| 12  | SARS-CoV-2 is a virus or a bacterium or an infection.   | TRUE     ||
| 13  | Vitamin C heals Covid-19.                               | FALSE    ||

    2. Covid-19 is caused by SARS-CoV-2.
    3. Covid-19 is caused by a Cornavirus.
    4. Covid-19 is caused by a Bacteria. - false
    5. Drinking alcohol prevents Covid-19
