package org.cardanofoundation.merkle.core;

import lombok.val;
import org.cardanofoundation.merkle.util.Hashing;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MerkleTreeBuilder {

    public static MerkleTree createFromHashes(List<byte[]> items) {
        return doFrom(items, items.size());
    }

    public static <T> MerkleTree createFromItems(List<T> items, Function<T, byte[]> serialiserFn) {
        return createFromHashes(items.stream().map(serialiserFn::apply).collect(Collectors.toList()));
    }

    private static MerkleTree doFrom(List<byte[]> items, int len) {
        if (items.isEmpty()) {
            return MerkleEmpty.EMPTY;
        }
        if (items.size() == 1) {
            return new MerkleLeaf(items.get(0));
        }

        val cutOff = len / 2;
        val left = doFrom(items.subList(0, cutOff), cutOff);
        val right = doFrom(items.subList(cutOff, items.size()), (len - cutOff));

        val hash = Hashing.combineHash(left.rootHash(), right.rootHash());

        return new MerkleNode(hash, left, right);
    }

}
