package com.redhat.persistence.jdo;

import com.redhat.persistence.oql.Expression;

import java.util.Collection;

interface OQLCollection extends Collection {
    Expression expression();
}
