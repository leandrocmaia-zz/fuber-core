package com.maia.fubercore.util;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;


@Getter
@ToString
public class Triplet<L, M, R> implements Serializable {

    L left;
    M middle;
    R right;

    public Triplet(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }
}
