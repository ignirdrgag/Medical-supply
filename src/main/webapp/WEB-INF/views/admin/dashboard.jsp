<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<div class="page-heading">
    <div>
        <span class="eyebrow">Administration</span>
        <h1>Gestion de la plateforme</h1>
        <p class="muted">Comptes, fournisseurs et catalogue des articles.</p>
    </div>
</div>

<section class="stats-grid">
    <article class="card stat"><span>Utilisateurs</span><strong>${userCount}</strong><small>comptes actifs</small></article>
    <article class="card stat"><span>Fournisseurs</span><strong>${supplierCount}</strong><small>partenaires</small></article>
    <article class="card stat"><span>Articles</span><strong>${itemCount}</strong><small>references</small></article>
    <article class="card stat"><span>Commandes</span><strong>${pendingOrders}</strong><small>en attente</small></article>
    <article class="card stat"><span>Mouvements</span><strong>${movementCount}</strong><small>sorties enregistrees</small></article>
</section>

<section class="section">
    <div class="section-heading">
        <h2>Utilisateurs</h2>
        <p>Chaque compte donne acces a un seul espace selon son role.</p>
    </div>
    <div class="table-wrap">
        <table class="admin-table">
            <thead>
            <tr><th>Identifiant</th><th>Nom complet</th><th>Role</th><th>Fournisseur</th><th>Actions</th></tr>
            </thead>
            <tbody>
            <c:forEach var="user" items="${users}">
                <tr>
                    <td><strong><c:out value="${user.username}"/></strong></td>
                    <td><c:out value="${user.fullName}"/></td>
                    <td><span class="role-badge role-${user.roleCssClass}"><c:out value="${user.roleLabel}"/></span></td>
                    <td><c:out value="${empty user.supplierName ? '-' : user.supplierName}"/></td>
                    <td class="actions"><div class="action-group">
                        <a class="btn btn-subtle btn-sm" href="${ctx}/admin/users/edit?id=${user.id}">Modifier</a>
                        <c:if test="${user.id != current.id}">
                            <form method="post" action="${ctx}/admin/users/delete" onsubmit="return confirm('Supprimer cet utilisateur ?');">
                                <input type="hidden" name="id" value="${user.id}">
                                <button class="btn btn-danger btn-sm" type="submit">Supprimer</button>
                            </form>
                        </c:if>
                        <c:if test="${user.id == current.id}"><span class="muted">Compte courant</span></c:if>
                    </div></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<section class="section">
    <div class="grid grid-2">
        <article class="card">
            <h2>Ajouter un utilisateur</h2>
            <form method="post" action="${ctx}/admin/users/add" class="form-grid">
                <label>Identifiant<input type="text" name="username" required></label>
                <label>Mot de passe<input type="password" name="password" minlength="4" required></label>
                <label>Nom complet<input type="text" name="fullName" required></label>
                <label>Role
                    <select name="role" required>
                        <option value="ADMIN">Administrateur</option>
                        <option value="STAFF">Agent de stock</option>
                        <option value="MANAGER">Responsable</option>
                        <option value="DOCTOR">Medecin</option>
                        <option value="SUPPLIER">Fournisseur</option>
                    </select>
                </label>
                <label class="span-2">Fournisseur associe (role fournisseur uniquement)
                    <select name="supplierId">
                        <option value="">Aucun</option>
                        <c:forEach var="supplier" items="${suppliers}">
                            <option value="${supplier.id}"><c:out value="${supplier.name}"/></option>
                        </c:forEach>
                    </select>
                </label>
                <div class="span-2"><button class="btn btn-primary" type="submit">Creer le compte</button></div>
            </form>
        </article>

        <article class="card">
            <h2>Ajouter un article</h2>
            <form method="post" action="${ctx}/admin/items/add" class="form-grid">
                <label>Nom<input type="text" name="name" required></label>
                <label>Categorie<input type="text" name="category" required></label>
                <label>Unite<input type="text" name="unit" required></label>
                <label>Seuil minimum<input type="number" name="minimum" min="0" value="0" required></label>
                <div class="span-2"><button class="btn btn-primary" type="submit">Ajouter l'article</button></div>
            </form>
        </article>
    </div>
</section>

<section class="section">
    <div class="section-heading">
        <h2>Fournisseurs</h2>
        <p>Partenaires disponibles pour les receptions et les commandes.</p>
    </div>
    <div class="table-wrap">
        <table class="admin-table">
            <thead><tr><th>Nom</th><th>Contact</th><th>E-mail</th><th>Actions</th></tr></thead>
            <tbody>
            <c:forEach var="supplier" items="${suppliers}">
                <tr>
                    <td><strong><c:out value="${supplier.name}"/></strong></td>
                    <td><c:out value="${supplier.contact}"/></td>
                    <td><c:out value="${supplier.email}"/></td>
                    <td class="actions"><div class="action-group">
                        <a class="btn btn-subtle btn-sm" href="${ctx}/admin/suppliers/edit?id=${supplier.id}">Modifier</a>
                        <form method="post" action="${ctx}/admin/suppliers/delete" onsubmit="return confirm('Supprimer ce fournisseur ?');">
                            <input type="hidden" name="id" value="${supplier.id}">
                            <button class="btn btn-danger btn-sm" type="submit">Supprimer</button>
                        </form>
                    </div></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <article class="card inset-card">
        <h2>Ajouter un fournisseur</h2>
        <form method="post" action="${ctx}/admin/suppliers/add" class="form-grid">
            <label>Nom<input type="text" name="name" required></label>
            <label>Contact<input type="text" name="contact" required></label>
            <label>E-mail<input type="email" name="email" required></label>
            <div><button class="btn btn-primary" type="submit">Ajouter</button></div>
        </form>
    </article>
</section>

<section class="section">
    <div class="section-heading">
        <h2>Catalogue des articles</h2>
        <p>Quantites calculees a partir des lots non perimes.</p>
    </div>
    <div class="table-wrap">
        <table class="admin-table">
            <thead>
            <tr><th>Article</th><th>Categorie</th><th>Unite</th><th>Stock utilisable</th><th>Seuil</th><th>Statut</th><th>Actions</th></tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${items}">
                <tr>
                    <td><strong><c:out value="${item.name}"/></strong></td>
                    <td><c:out value="${item.category}"/></td>
                    <td><c:out value="${item.unit}"/></td>
                    <td>${item.usableQuantity}</td>
                    <td>${item.minimumStock}</td>
                    <td>
                        <span class="pill ${item.lowStock ? 'pill-danger' : 'pill-ok'}">
                            ${item.lowStock ? 'Stock bas' : 'OK'}
                        </span>
                    </td>
                    <td class="actions"><div class="action-group">
                        <a class="btn btn-subtle btn-sm" href="${ctx}/admin/items/edit?id=${item.id}">Modifier</a>
                        <form method="post" action="${ctx}/admin/items/delete" onsubmit="return confirm('Supprimer cet article ? Les lots existants seront aussi supprimes.');">
                            <input type="hidden" name="id" value="${item.id}">
                            <button class="btn btn-danger btn-sm" type="submit">Supprimer</button>
                        </form>
                    </div></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</section>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
