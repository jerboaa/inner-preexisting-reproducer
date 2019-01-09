## Steps to reproduce

    $ javac InnerPreexistence.java
    $ java InnerPreexistence
    DEBUG: Waking up: 1

The second step never completes. Contrast this with `-Xint`

    $ java -Xint InnerPreexistence
    DEBUG: Waking up: 1
    Test passed!

## Fix

Uncomment `volatile` in declaration of `keepOnGoing`:

    $ javac InnerPreexistence.java
    $ java InnerPreexistence
    DEBUG: Waking up: 1
    Test passed!   
