# Névoa — navegador privado experimental

**Névoa** é um projeto de navegador Android com sessão efêmera. O site institucional fica em `/docs` e pode ser publicado no GitHub Pages. O APK é compilado pelo GitHub Actions.

## Privacidade: limites importantes
O app busca reduzir dados armazenados localmente; não garante anonimato absoluto. Sites ainda podem ver seu IP, características do navegador e dados enviados. Operadoras, redes Wi-Fi, DNS e serviços acessados podem registrar metadados. O motor é o Android System WebView, não um motor próprio. Não use como substituto de Tor/VPN quando isso fizer parte do seu modelo de ameaça.

## Publicar o site
1. Abra **Settings → Pages**.
2. Em **Build and deployment**, selecione **Deploy from a branch**.
3. Selecione branch `main` e pasta `/docs`, depois salve.
4. URL esperada: https://vnsants.github.io/nevoa-browser/

## Gerar o APK
1. Abra **Actions** e selecione **Build Android APK**.
2. Execute o workflow manualmente ou faça um push na branch `main`.
3. Baixe o artefato `nevoa-debug-apk` quando o job terminar.
4. É um APK de debug para testes; distribuição pública exige assinatura de release.

## Limitações
- Alguns sites bloqueiam WebView ou dependem de APIs incompatíveis.
- Cookies desativados podem impedir login ou quebrar funcionalidades.
- O Android System WebView e o sistema operacional ficam fora do controle deste app.
