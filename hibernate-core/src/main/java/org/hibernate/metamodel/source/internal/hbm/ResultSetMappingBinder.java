/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.hibernate.metamodel.source.internal.hbm;

import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.metamodel.source.internal.jaxb.hbm.JaxbLoadCollectionElement;
import org.hibernate.metamodel.source.internal.jaxb.hbm.JaxbResultsetElement;
import org.hibernate.metamodel.source.internal.jaxb.hbm.JaxbReturnElement;
import org.hibernate.metamodel.source.internal.jaxb.hbm.JaxbReturnJoinElement;
import org.hibernate.metamodel.source.internal.jaxb.hbm.JaxbReturnScalarElement;
import org.hibernate.metamodel.spi.InFlightMetadataCollector;
import org.hibernate.metamodel.spi.LocalBindingContext;
import org.hibernate.type.Type;

public class ResultSetMappingBinder {
	public static ResultSetMappingDefinition buildResultSetMappingDefinitions(
			final JaxbResultsetElement element,
			final LocalBindingContext bindingContext,
			final InFlightMetadataCollector metadataCollector) {
		final ResultSetMappingDefinition definition = new ResultSetMappingDefinition( element.getName() );
		int cnt = 0;
		for ( final JaxbReturnScalarElement r : element.getReturnScalar() ) {
			String column = r.getColumn();
			String typeFromXML = r.getType();
			Type type = StringHelper.isNotEmpty( typeFromXML ) ? metadataCollector.getTypeResolver()
					.heuristicType( typeFromXML ) : null;
			definition.addQueryReturn( new NativeSQLQueryScalarReturn( column, type ) );
		}
		for ( final JaxbReturnElement r : element.getReturn() ) {
			definition.addQueryReturn( new ReturnBinder( r, cnt++, bindingContext, metadataCollector ).process() );
		}
		for ( final JaxbReturnJoinElement r : element.getReturnJoin() ) {
			definition.addQueryReturn( new ReturnJoinBinder( r, cnt++, bindingContext, metadataCollector ).process() );
		}
		for ( final JaxbLoadCollectionElement r : element.getLoadCollection() ) {
			definition.addQueryReturn( new LoadCollectionBinder( r, cnt++, bindingContext, metadataCollector ).process() );
		}

		return definition;
	}
}