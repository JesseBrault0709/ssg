package com.jessebrault.ssg

import org.junit.jupiter.api.Test
import picocli.CommandLine

import static org.junit.jupiter.api.Assertions.assertEquals

final class StaticSiteGeneratorCliTests {

    private static void cliSmokeScreen(String... args) {
        assertEquals(0, new CommandLine(StaticSiteGeneratorCli).with {
            caseInsensitiveEnumValuesAllowed = true
            execute(args)
        })
    }

    @Test
    void helpSmokeScreen() {
        cliSmokeScreen('--help')
    }

    @Test
    void initHelpSmokeScreen() {
        cliSmokeScreen('init', '--help')
    }

    @Test
    void buildHelpSmokeScreen() {
        cliSmokeScreen('build', '--help')
    }

}
