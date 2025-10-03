/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2025
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package one.oktw.galaxy.item

import one.oktw.galaxy.item.category.CustomItemCategory

class CustomItemBrowser(val isCreative: Boolean = false) {
    private val categories = if (isCreative) CustomItemCategory.creativeCategories else CustomItemCategory.categories
    private var currentIndex = 0
    private var currentPageOffset = 0
    private var pageUpdateListener: (() -> Unit)? = null

    // Category paging
    fun getCategoryGui(): List<CustomItemCategory> {
        return listOf(
            if (currentIndex == 0) categories.last() else categories[currentIndex - 1],
            categories[currentIndex],
            if (currentIndex == categories.lastIndex) categories.first() else categories[currentIndex + 1]
        )
    }

    fun previousCategory() {
        if (currentIndex == 0) {
            currentIndex = categories.lastIndex
        } else {
            currentIndex -= 1
        }
        currentPageOffset = 0
        pageUpdateListener?.invoke()
    }

    fun nextCategory() {
        if (currentIndex == categories.lastIndex) {
            currentIndex = 0
        } else {
            currentIndex += 1
        }
        currentPageOffset = 0
        pageUpdateListener?.invoke()
    }

    // Category item paging
    fun getCurrent() = categories[currentIndex]
    private fun getAllPagedItems(): List<List<CustomItem>> = getCurrent().items.chunked(6)

    fun getCategoryItems() =
        getAllPagedItems()
            .drop(currentPageOffset)
            .take(3)
            .flatten()

    fun isPreviousPageAvailable() = currentPageOffset > 0
    fun isNextPageAvailable() = currentPageOffset + 2 < getAllPagedItems().lastIndex

    fun previousPage() {
        if (isPreviousPageAvailable()) {
            currentPageOffset -= 1
        }
        pageUpdateListener?.invoke()
    }

    fun nextPage() {
        if (isNextPageAvailable()) {
            currentPageOffset += 1
        }
        pageUpdateListener?.invoke()
    }

    fun getItemByIndex(index: Int) = getCategoryItems().getOrNull(index)

    fun onPageUpdate(action: () -> Unit) {
        pageUpdateListener = action
    }
}
