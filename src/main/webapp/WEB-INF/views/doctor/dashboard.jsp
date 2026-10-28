<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<div class="page-heading">
    <div>
        <span class="eyebrow">Espace medecin</span>
        <h1>Disponibilite des produits</h1>
        <p class="muted">Consultez les quantites disponibles avant de prendre en charge un patient.</p>
    </div>
    <span class="role-badge role-doctor">Consultation uniquement</span>
</div>

<section class="stats-grid">
    <article class="card stat"><span>Produits au catalogue</span><strong>${fn:length(items)}</strong><small>references suivies</small></article>
    <article class="card stat"><span>Produits sous le seuil</span><strong>${lowStockCount}</strong><small>stock a surveiller</small></article>
    <article class="card stat"><span>Ruptures</span><strong>${outOfStockCount}</strong><small>aucune quantite utilisable</small></article>
</section>

<section class="section">
    <div class="section-heading">
        <h2>Stock disponible</h2>
        <p>La quantite utilisable exclut les lots expires. Contactez le staff pour toute mise a jour.</p>
    </div>
    <div class="table-wrap">
        <table>
            <thead>
            <tr><th>Produit</th><th>Categorie</th><th>Quantite disponible</th><th>Stock total</th><th>Seuil minimum</th><th>Etat</th></tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${empty items}">
                    <tr><td colspan="6" class="muted center">Aucun produit dans le catalogue.</td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="item" items="${items}">
                        <tr>
                            <td><strong><c:out value="${item.name}"/></strong></td>
                            <td><c:out value="${item.category}"/></td>
                            <td><strong>${item.usableQuantity}</strong> <c:out value="${item.unit}"/></td>
                            <td>${item.totalQuantity} <c:out value="${item.unit}"/></td>
                            <td>${item.minimumStock} <c:out value="${item.unit}"/></td>
                            <td>
                                <span class="pill ${item.outOfStock ? 'pill-danger' : (item.lowStock ? 'pill-warn' : 'pill-ok')}">
                                    ${item.outOfStock ? 'Rupture' : (item.lowStock ? 'Stock bas' : 'Disponible')}
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</section>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
