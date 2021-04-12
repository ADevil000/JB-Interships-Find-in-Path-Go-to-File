# JB-Interships-Find-in-Path-Go-to-File
Репозиторий для тестового задания от JB "Find in Path"

Требуется реализовать простой планировщик задач. На вход данному планировщику передается набор задач, которые он должен выполнить.

Задача может обладать зависимостями, то есть набором задач, которые требуется выполнить до выполнения данной.

Задача представляется интерфейсом:

   ```
   interface Task {
      // выполняет задачу
      void execute();

       // возвращает зависимости для данной задачи
      Collection<Task> dependencies();
   }
   ```

Требуется написать исходный код класса, реализуещего планировщик:

   ```
   class TaskExecutor {
       void execute(Collection<Task> tasks) {
          // реализация
       }
   }
   ```
Вы можете делать дополнительные предположения, которые вам кажутся необходимыми. Большим плюсом будет, если планировщик будет многопоточным. Данный планировщик должен быть устойчив к некорректным входным данным.
  
Класс Main содержит простой пример с реализацией EasyTask. 

Решение основано на том, что корректным графом для решения зависисмостей будет ациклический. Из данного списка задач, строится полный граф зависимостей, в который будут добавлены даже те задачи, которые в изначальном списке не указаны. Строится аналогия к топологической сортировке, так  как в данном ориентированном графе может быть не один корень. Для построения плана используются пометки ожидания, которые говорят, что в план выполнения добавлены все необходимые зависимости. (Последующим осознанием, понял, что можно написать сам topsort на данном графе, поэтому построенние плана может быть сокращено до O(количество ребер))
