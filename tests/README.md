# Test sentences

Consider the knowledge base `ontos/fact.owl`. Below is a list of sentences for testing the different features of the
program.

| #  | Text                                                    | Decision | Comment                               |
|----|---------------------------------------------------------|----------|---------------------------------------|
| 1  | Viruses are not bacteria.                               | TRUE     | Type Negation Pattern - plural        |
| 2  | A virus is not a bacterium.                             | TRUE     | Type Negation Pattern - singular      |
| 3  | Bacteria are viruses.                                   | FALSE    | Positive form                         |
| 4  | Antibiotics kill viruses.                               | FALSE    | VerbNet Role Pattern: Agent, Patient  |
| 5  | Bacitracin kills viruses.                               | FALSE    | 4 + WordNet                           |
| 6  | Amoxicillin kills retrovirus.                           | FALSE    | 4 + WordNet                           |
| 7  | Amoxicillin kills retrovirus. Bacteria are not viruses. | FALSE    | Composed: 1 + 6, with entailed axioms |
| 8  | Covid-19 is caused by SARS-CoV-2.                       | TRUE     | VerbNet Role Pattern: Theme1, Theme2  |
| 9  | Covid-19 is caused by a coronavirus.                    | TRUE     | 8 + concept matching                  |
| 10 | Covid-19 is caused by a bacterium.                      | FALSE    |                                       |
| 11 | SARS-Cov-2 is a virus and a bacterium.                  | FALSE    |                                       |
| 12 | SARS-CoV-2 is a virus or a bacterium or an infection.   | TRUE     | Type disjunction pattern              |
| 13 | Vitamin C heals Covid-19.                               | FALSE    |                                       |
| 14 | Sars-CoV-2 is a virus.                                  | TRUE     |                                       |
| 15 | Sars-CoV-2 is not a virus.                              | FALSE    |                                       |
| 16 | Sars-CoV-2 is not a Bacterium.                          | TRUE     |                                       |

[//]: # (| 17  | Bacitracin is not a disease.                            | TRUE     ||)

[//]: # (| 18  | Bacitracin is a vaccine.                                | FALSE    ||)

[//]: # (  5. Drinking alcohol prevents Covid-19)

The following execution times were measured for these tests:

| #  | CLI execution time [s] | Web service response time [s] |
|----|------------------------|-------------------------------|
| 1  | 5.079                  | 1.44063                       |
| 2  | 6.757                  | 2.633247                      |
| 3  | 5.939                  | 1.461982                      |
| 4  | 7.134                  | 3.525881                      |
| 5  | 7.865                  | 2.390111                      |
| 6  | 7.335                  | 2.981617                      |
| 7  | 8.344                  | 3.301598                      |
| 8  | 6.894                  | 4.964068                      |
| 9  | 7.503                  | 2.431574                      |
| 10 | 8.452                  | 5.084182                      |
| 11 | 8.713                  | 3.718351                      |
| 12 | 7.367                  | 4.245525                      |
| 13 | 6.876                  | 2.045225                      |
| 14 | 6.123                  | 2.690363                      |
| 15 | 7.361                  | 4.82036                       |
| 16 | 7.617                  | 3.305818                      |
| 17 | 6.267                  | 2.677611                      |
| 18 | 6.788                  | 2.311747                      |
