package qunar.tc.bistoury.instrument.client.spring.el;

/**
 * Captures the possible configuration settings for a compiler that can be
 * used when evaluating expressions.
 *
 * @author Andy Clement
 * @since 4.1
 */
public enum SpelCompilerMode {

    /**
     * The compiler is switched off; this is the default.
     */
    OFF,

    /**
     * In immediate mode, expressions are compiled as soon as possible (usually after 1 interpreted run).
     * If a compiled expression fails it will throw an exception to the caller.
     */
    IMMEDIATE,

    /**
     * In mixed mode, expression evaluation silently switches between interpreted and compiled over time.
     * After a number of runs the expression gets compiled. If it later fails (possibly due to inferred
     * type information changing) then that will be caught internally and the system switches back to
     * interpreted mode. It may subsequently compile it again later.
     */
    MIXED;
}

