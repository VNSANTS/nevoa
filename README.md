# Névoa — navegador Android experimental

**Névoa** é um projeto de navegador Android com foco em reduzir rastros armazenados localmente. O site institucional é publicado pelo GitHub Actions (GitHub Pages), e o APK é compilado no mesmo workflow.

## Site
Quando o deploy terminar, acesse: https://vnsants.github.io/nevoa/

## Publicar o site
1. Abra **Settings → Pages**.
2. Em **Build and deployment → Source**, selecione **GitHub Actions**.
3. O workflow `Build APK and Deploy Site` publica automaticamente a pasta `docs/` a cada push na branch `main`.
4. Acompanhe a execução em **Actions**. A primeira publicação pode levar alguns minutos.

## Gerar o APK
1. Abra **Actions** e selecione **Build APK and Deploy Site**.
2. Execute o workflow manualmente ou faça um push na branch `main`.
3. Quando o job **Build Android APK** terminar com sucesso, baixe o artefato `nevoa-debug-apk` na página da execução.
4. É um APK de debug para testes; distribuição pública exige assinatura de release.

## Privacidade: limites importantes
O app busca reduzir dados armazenados localmente; não garante anonimato absoluto. Sites ainda podem ver seu IP, características do navegador e dados enviados. Redes, operadoras, DNS e serviços acessados podem registrar metadados. O motor é o Android System WebView, não um motor próprio. Não use como substituto de Tor/VPN quando isso fizer parte do seu modelo de ameaça.

## Limitações
- Alguns sites bloqueiam WebView ou dependem de APIs incompatíveis.
- Cookies desativados podem impedir login ou quebrar funcionalidades.
- O Android System WebView e o sistema operacional ficam fora do controle deste app.
