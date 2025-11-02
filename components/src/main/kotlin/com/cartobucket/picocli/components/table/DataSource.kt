package com.cartobucket.picocli.components.table

/**
 * Interface for data access in VirtualTable
 * Supports both in-memory and lazy-loaded data sources
 */
interface DataSource<T> {
    /**
     * Total number of rows in the data source
     */
    val size: Int

    /**
     * Get a single row by index
     */
    fun get(index: Int): T

    /**
     * Get a range of rows
     * @param start Starting index (inclusive)
     * @param count Number of rows to fetch
     * @return List of rows (may be smaller than count if near the end)
     */
    fun getRange(start: Int, count: Int): List<T>
}

/**
 * In-memory data source backed by a List
 */
class InMemoryDataSource<T>(
    private val data: List<T>
) : DataSource<T> {
    override val size: Int
        get() = data.size

    override fun get(index: Int): T {
        require(index in data.indices) { "Index $index out of bounds for size $size" }
        return data[index]
    }

    override fun getRange(start: Int, count: Int): List<T> {
        if (start >= data.size) return emptyList()

        val end = (start + count).coerceAtMost(data.size)
        return data.subList(start, end)
    }
}

/**
 * Lazy data source that fetches data on demand
 * Useful for paginated or streaming data
 */
class LazyDataSource<T>(
    override val size: Int,
    private val fetcher: (offset: Int, limit: Int) -> List<T>
) : DataSource<T> {
    private val cache = mutableMapOf<Int, T>()

    override fun get(index: Int): T {
        require(index in 0 until size) { "Index $index out of bounds for size $size" }

        return cache.getOrPut(index) {
            val results = fetcher(index, 1)
            results.firstOrNull() ?: throw IllegalStateException("Fetcher returned empty result for index $index")
        }
    }

    override fun getRange(start: Int, count: Int): List<T> {
        if (start >= size) return emptyList()

        val actualCount = count.coerceAtMost(size - start)
        val results = fetcher(start, actualCount)

        // Cache the results
        results.forEachIndexed { offset, item ->
            cache[start + offset] = item
        }

        return results
    }

    /**
     * Clear the cache
     */
    fun clearCache() {
        cache.clear()
    }
}
