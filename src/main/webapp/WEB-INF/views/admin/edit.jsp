<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<div class="page-heading">
    <div>
        <span class="eyebrow">Administration</span>
        <h1>Modifier les donnees</h1>
        <p class="muted">Mettez a jour les informations puis enregistrez les changements.</p>
    </div>
    <a class="btn btn-subtle" href="${ctx}/admin/dashboard">Retour au tableau de bord</a>
</div>

<section class="section">
    <c:choose>
        <c:when test="${editType eq 'user'}">
            <article class="card edit-card">
                <h2>Modifier un utilisateur</h2>
                <form method="post" action="${ctx}/admin/users/update" class="form-grid">
                    <input type="hidden" name="id" value="${entity.id}">
                    <label>Identifiant<input type="text" name="username" value="<c:out value='${entity.username}'/>" required></label>
                    <label>Nom complet<input type="text" name="fullName" value="<c:out value='${entity.fullName}'/>" required></label>
                    <label>Nouveau mot de passe<input type="password" name="password" minlength="4" placeholder="Laisser vide pour conserver"></label>
                    <label>Role
                        <select name="role" required>
                            <c:forEach var="roleOption" items="${roles}">
                                <c:choose>
                                    <c:when test="${entity.roleName eq roleOption.code}">
                                        <option value="${roleOption.code}" selected><c:out value="${roleOption.label}"/></option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${roleOption.code}"><c:out value="${roleOption.label}"/></option>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </label>
                    <label class="field-full">Fournisseur associe (role fournisseur uniquement)
                        <select name="supplierId">
                            <option value="">Aucun</option>
                            <c:forEach var="supplier" items="${suppliers}">
                                <c:choose>
                                    <c:when test="${entity.supplierId eq supplier.id}">
                                        <option value="${supplier.id}" selected><c:out value="${supplier.name}"/></option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${supplier.id}"><c:out value="${supplier.name}"/></option>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </label>
                    <div class="form-actions field-full">
                        <button class="btn btn-primary" type="submit">Enregistrer</button>
                    </div>
                </form>
            </article>
        </c:when>
        <c:when test="${editType eq 'supplier'}">
            <article class="card edit-card">
                <h2>Modifier un fournisseur</h2>
                <form method="post" action="${ctx}/admin/suppliers/update" class="form-grid">
                    <input type="hidden" name="id" value="${entity.id}">
                    <label>Nom<input type="text" name="name" value="<c:out value='${entity.name}'/>" required></label>
                    <label>Contact<input type="text" name="contact" value="<c:out value='${entity.contact}'/>" required></label>
                    <label class="field-full">E-mail<input type="email" name="email" value="<c:out value='${entity.email}'/>" required></label>
                    <div class="form-actions field-full">
                        <button class="btn btn-primary" type="submit">Enregistrer</button>
                    </div>
                </form>
            </article>
        </c:when>
        <c:otherwise>
            <article class="card edit-card">
                <h2>Modifier un article</h2>
                <form method="post" action="${ctx}/admin/items/update" class="form-grid">
                    <input type="hidden" name="id" value="${entity.id}">
                    <label>Nom<input type="text" name="name" value="<c:out value='${entity.name}'/>" required></label>
                    <label>Categorie<input type="text" name="category" value="<c:out value='${entity.category}'/>" required></label>
                    <label>Unite<input type="text" name="unit" value="<c:out value='${entity.unit}'/>" required></label>
                    <label>Seuil minimum<input type="number" name="minimum" min="0" value="${entity.minimumStock}" required></label>
                    <div class="form-actions field-full">
                        <button class="btn btn-primary" type="submit">Enregistrer</button>
                    </div>
                </form>
            </article>
        </c:otherwise>
    </c:choose>
</section>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
