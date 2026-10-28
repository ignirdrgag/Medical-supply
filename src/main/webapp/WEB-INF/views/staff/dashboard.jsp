<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<div class="page-heading">
    <div>
        <span class="eyebrow">Agent de stock</span>
        <h1>Receptions et sorties</h1>
        <p class="muted">Enregistrez les entrees de lots et les sorties vers les services.</p>
    </div>
</div>

<section class="stats-grid">
    <article class="card stat"><span>Articles suivis</span><strong>${fn:length(items)}</strong><small>references</small></article>
    <article class="card stat"><span>Alertes stock</span><strong>${fn:length(lowStockItems)}</strong><small>sous le seuil</small></article>
    <article class="card stat"><span>Lots a surveiller</span><strong>${fn:length(expiringLots)}</strong><small>expiration sous 30 jours</small></article>
    <article class="card stat"><span>Mouvements</span><strong>${movementCount}</strong><small>sorties enregistrees</small></article>
</section>

<section class="section">
    <div class="grid grid-2">
        <article class="card">
            <h2>Receptionner un lot</h2>
            <form method="post" action="${ctx}/staff/receive" class="form-grid">
                <label>Article
                    <select name="itemId" required>
                        <option value="">Choisir un article</option>
                        <c:forEach var="item" items="${items}">
                            <option value="${item.id}"><c:out value="${item.name}"/></option>
                        </c:forEach>
                    </select>
                </label>
                <label>Fournisseur
                    <select name="supplierId" required>
                        <option value="">Choisir un fournisseur</option>
                        <c:forEach var="supplier" items="${suppliers}">
                            <option value="${supplier.id}"><c:out value="${supplier.name}"/></option>
                        </c:forEach>
                    </select>
                </label>
                <label>Quantite recue<input type="number" name="quantity" min="1" value="1" required></label>
                <label>Date d'expiration<input type="date" name="expiration" required></label>
                <div class="span-2"><button class="btn" type="submit">Enregistrer la reception</button></div>
            </form>
        </article>

        <article class="card">
            <h2>Sortir du stock</h2>
            <form method="post" action="${ctx}/staff/issue" class="form-grid">
                <label>Article
                    <select name="itemId" required>
                        <option value="">Choisir un article</option>
                        <c:forEach var="item" items="${items}">
                            <option value="${item.id}">
                                <c:out value="${item.name}"/> (${item.usableQuantity} disponibles)
                            </option>
                        </c:forEach>
                    </select>
                </label>
                <label>Quantite a sortir<input type="number" name="quantity" min="1" value="1" required></label>
                <label class="span-2">Service / destination<input type="text" name="destination" required></label>
                <div class="span-2">
                    <p class="hint">La sortie applique la regle FEFO : le lot qui expire le plus tot est consomme en premier.</p>
                    <button class="btn" type="submit">Enregistrer la sortie</button>
                </div>
            </form>
        </article>
    </div>
</section>

<section class="section">
    <div class="section-heading">
        <h2>Inventaire</h2>
        <p>Quantites totales, utilisables et perimees par article.</p>
    </div>
    <div class="table-wrap">
        <table>
            <thead>
            <tr><th>Article</th><th>Categorie</th><th>Total</th><th>Utilisable</th><th>Perime</th><th>Seuil</th><th>Statut</th></tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${items}">
                <tr>
                    <td><strong><c:out value="${item.name}"/></strong></td>
                    <td><c:out value="${item.category}"/></td>
                    <td>${item.totalQuantity} <c:out value="${item.unit}"/></td>
                    <td>${item.usableQuantity}</td>
                    <td>${item.expiredQuantity}</td>
                    <td>${item.minimumStock}</td>
                    <td>
                        <span class="pill ${item.outOfStock ? 'pill-danger' : (item.lowStock ? 'pill-warn' : 'pill-ok')}">
                            ${item.outOfStock ? 'Rupture' : (item.lowStock ? 'Stock bas' : 'OK')}
                        </span>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<section class="section">
    <div class="grid grid-2">
        <article class="card">
            <h2>Alertes de stock</h2>
            <c:choose>
                <c:when test="${empty lowStockItems}">
                    <p class="muted">Aucun article sous le seuil.</p>
                </c:when>
                <c:otherwise>
                    <ul class="simple-list">
                        <c:forEach var="item" items="${lowStockItems}">
                            <li>
                                <strong><c:out value="${item.name}"/></strong>
                                <span>${item.usableQuantity} / seuil ${item.minimumStock}</span>
                            </li>
                        </c:forEach>
                    </ul>
                </c:otherwise>
            </c:choose>
        </article>

        <article class="card">
            <h2>Lots proches de l'expiration</h2>
            <c:choose>
                <c:when test="${empty expiringLots}">
                    <p class="muted">Aucun lot n'expire dans les 30 prochains jours.</p>
                </c:when>
                <c:otherwise>
                    <ul class="simple-list">
                        <c:forEach var="lot" items="${expiringLots}">
                            <li>
                                <strong><c:out value="${lot.itemName}"/></strong>
                                <span>${lot.quantity} unite(s) - expire le <c:out value="${lot.expirationDisplay}"/></span>
                            </li>
                        </c:forEach>
                    </ul>
                </c:otherwise>
            </c:choose>
        </article>
    </div>
</section>

<section class="section">
    <div class="section-heading">
        <h2>Derniers mouvements</h2>
        <p>Historique des sorties de stock.</p>
    </div>
    <div class="table-wrap">
        <table>
            <thead><tr><th>Date</th><th>Article</th><th>Quantite</th><th>Destination</th><th>Agent</th></tr></thead>
            <tbody>
            <c:choose>
                <c:when test="${empty movements}">
                    <tr><td colspan="5" class="muted center">Aucun mouvement enregistre.</td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="movement" items="${movements}">
                        <tr>
                            <td><c:out value="${movement.movedAtDisplay}"/></td>
                            <td><c:out value="${movement.itemName}"/></td>
                            <td>${movement.quantity}</td>
                            <td><c:out value="${movement.destination}"/></td>
                            <td><c:out value="${movement.userName}"/></td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</section>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
