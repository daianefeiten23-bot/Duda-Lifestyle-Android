DUDA LIFESTYLE — Android WebView

Já incluído:
- index.html original em app/src/main/assets/index.html
- WebView com JavaScript e LocalStorage
- INTERNET
- RECORD_AUDIO + solicitação de permissão
- Google Mobile Ads
- Rewarded Ad usando IDs oficiais de TESTE do Google

Ponte disponível no HTML:
  Android.showRewardedAd()

Eventos enviados ao HTML:
  dudaRewardEarned
  dudaAdUnavailable

IMPORTANTE:
Antes de publicar, trocar:
1) APPLICATION_ID de teste no AndroidManifest.xml pelo App ID real do AdMob.
2) REWARDED_TEST_ID no MainActivity.java pelo ID real do bloco Rewarded.
