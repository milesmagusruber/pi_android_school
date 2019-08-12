# PI-school 2019

## Week 3
 - Сохранение данных на диске

   - https://developer.android.com/guide/topics/data/data-storage
   - https://developer.android.com/training/data-storage/shared-preferences 
   - https://developer.android.com/training/data-storage/sqlite.html
   - https://developer.android.com/training/data-storage/room
   - https://youtu.be/A-P6EDw5z_s
   - http://facebook.github.io/stetho/ Для работы с БД в живую на девайсе

- Задания и codelabs
  - https://codelabs.developers.google.com/codelabs/android-training-livedata-viewmodel/index.html?index=..%2F..android-training#0
  - https://codelabs.developers.google.com/codelabs/android-training-room-delete-data/index.html?index=..%2F..android-training#0
  - Работаем на основе приложения с прошлой недели
    - В этот раз не используем никакую ORM типа Room
    - Используем базовые классы типа SqliteDatabaseHelper, Cursor etc.
    - При выходе из приложения сохраняем текст введенный в поисковую строку
    - Теперь на экране просмотра фотографии нужно добавить функцию - сохранения фотографии в избранные
    - Добавить возможность на главном экране перейти на экран с сохраненными ссылками и к какому запросу они относились. Пример: "Земля " и список линок подобно тому как вы уже выводите при поиске и так 
    - Если пользователь будет просматривать фотографию которая уже сохраненна в избранные то на экране просмотра добавить возможность удаления этой фотографии из избранных (бонус - обрабатывать оба состояния сразу же: например зашли на незалайканую фотографию - лайкнуть и сразу же отобразить возможность ее удаления)
    - На экране просмотра фотографии нужно выводить текст поискового запроса по которому была найдена фотография
    - Создать экран где можно просмотреть последние 20 запросов
    - Челендж - сделать экран логина (формальный без пароля) и сохранять все данные на каждого отлдельно взятого пользователя. Если потом выбираем сохраненного пользователя то должны соответственно иметь возможность просмотреть все его последние запросы, какие фотки он полайкал
    
## Week 4
 - Списки
 
   - https://www.youtube.com/watch?v=G35pcPv_tEA
   - https://developer.android.com/guide/topics/ui/layout/recyclerview
   - https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.html
   - https://developer.android.com/reference/android/support/v7/widget/helper/ItemTouchHelper
   - https://github.com/bumptech/glide
  - Задания и codelabs
    - https://codelabs.developers.google.com/codelabs/android-training-create-recycler-view/index.html?index=..%2F..android-training#0
    - Работаем на основе приложения с прошлой недели:
      - После получения результата отображаем все данные в списке
      - Отображаем результаты в карточке
      - На каждой карточке должна отображаться картинка
      - На каждой карточке должен отображаться запрос по которому картинка получена
      - Если карточка не понравилась то юзер должен иметь возможность свайпнуть ее в сторону (удалить из списка)
      - На экране Favourites теперь тоже отображаем список с фоточками аналогичный главному экрану
      - Если на экране Favourites пользователь свайпает полайканую фотографию то ее необходимо удалить из списка favourites и в базе данных
      - На экране Favourites так же нужно на каждой фотографии отобразить кнопку на элементе списка которая позволить убрать фотографию из списка (помимо свайпа)
      - На экране Favourites запрос по которому лайкались фотки - отдельный элемент списка после которого идут все фотографии которые ему соответствуют
      - Список запросов теперь тоже нужно оформить как список с элеиментами, а не как строку
      - На главном экране в случае наличия интернета когда пользователь близок к достижению конца списка необходимо реализовать бесконечный список (в случае наличия данных на API) - подгрузить новые данные и отобразить в конце списка


Дополнительные материалы:
Официальная документация https://developer.android.com/

Google codelabs https://codelabs.developers.google.com/android-training/

Yandex школа мобильной разработки https://www.youtube.com/playlist?list=PLQC2_0cDcSKBNCR8UWeElzCUuFkXASduz

Vogella https://www.vogella.com//tutorials/android.html

Codepath https://guides.codepath.com/android

Busy Coder's guide to Android development https://commonsware.com/Android/ (book samples) https://github.com/commonsguy/cw-omnibus
