<%@ include file="/WEB-INF/views/fragments/header.jspf" %>
<section class="error-page">
    <span class="page-kicker">MediSupply</span>
    <span class="error-code">500</span>
    <h1>Erreur serveur</h1>
    <p class="muted">La page n'a pas pu etre chargee. Revenez a l'accueil ou reessayez dans quelques instants.</p>
    <a class="btn btn-primary" href="${ctx}/home">Retour a l'accueil</a>
</section>
<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
