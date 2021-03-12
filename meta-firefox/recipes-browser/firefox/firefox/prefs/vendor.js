pref("general.useragent.compatMode.firefox", true);
pref("distribution.searchplugins.defaultLocale", "en-US");
pref("browser.shell.checkDefaultBrowser", true);
pref("browser.shell.skipDefaultBrowserCheckOnFirstRun", true);
pref("devtools.webide.autoinstallADBHelper", false);
pref("devtools.webide.autoinstallFxdtAdapters", false);
// Forbid application updates
lockPref("app.update.enabled", false);
lockPref("extensions.update.enabled", false);
lockPref("extensions.autoDisableScopes", 10);
