package com.dicoding.habitapp.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dicoding.habitapp.utils.HabitSortType
import com.dicoding.habitapp.utils.SortUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HabitRepository(private val habitDao: HabitDao, private val executor: ExecutorService) {

    companion object {

        @Volatile
        private var instance: HabitRepository? = null
        private var INITIAL_SIZE = 4
        private var PAGE_SIZE = 10

        fun getInstance(context: Context): HabitRepository {
            return instance ?: synchronized(this) {
                if (instance == null) {
                    val database = HabitDatabase.getInstance(context)
                    instance = HabitRepository(
                        database.habitDao(),
                        Executors.newSingleThreadExecutor()
                    )
                }
                return instance as HabitRepository
            }

        }
    }

    //TODO 4 : Use SortUtils.getSortedQuery to create sortable query and build paged list
    fun getHabits(sortType: HabitSortType): LiveData<PagedList<Habit>> {
        //throw NotImplementedError("Not yet implemented")
       val configuration = PagedList.Config.Builder()
           .setEnablePlaceholders(true)
           .setInitialLoadSizeHint(INITIAL_SIZE)
           .setPageSize(PAGE_SIZE)
           .build()
        return LivePagedListBuilder(habitDao.getHabits(SortUtils.getSortedQuery(sortType)), configuration).build()
    }

    //TODO 5 : Complete other function inside repository
    fun getHabitById(habitId: Int): LiveData<Habit> = habitDao.getHabitById(habitId)
        //throw NotImplementedError("Not yet implemented")

    fun insertHabit(newHabit: Habit) = habitDao.insertHabit(habit = newHabit)
        //throw NotImplementedError("Not yet implemented")

    fun deleteHabit(habit: Habit) {
        executor.execute {
            habitDao.deleteHabit(habit)
        }
    }

    fun getRandomHabitByPriorityLevel(level: String): LiveData<Habit> = habitDao.getRandomHabitByPriorityLevel(level)
        //throw NotImplementedError("Not yet implemented")

}