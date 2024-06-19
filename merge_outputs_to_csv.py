import re
import csv

# Regular expressions to match the output lines
size_re = re.compile(r'Test size: (\d+)')
skip_list_insert_re = re.compile(r'Skip list insertion took (\d+) ms.')
skip_list_delete_re = re.compile(r'Skip list deletion took (\d+) ms.')
skip_list_action_re = re.compile(r'Skip list actions took (\d+) ms.')
tree_set_insert_re = re.compile(r'Tree set insertion took (\d+) ms.')
tree_set_delete_re = re.compile(r'Tree set deletion took (\d+) ms.')
tree_set_action_re = re.compile(r'Tree set actions took (\d+) ms.')

def process_file(filename):
    data = []
    with open(filename, 'r') as infile:
        test_size = None
        skip_list_insert = None
        skip_list_delete = None
        skip_list_actions = None
        tree_set_insert = None
        tree_set_delete = None
        tree_set_actions = None

        for line in infile:
            size_match = size_re.match(line)
            if size_match:
                test_size = int(size_match.group(1))
            skip_list_insert_match = skip_list_insert_re.match(line)
            if skip_list_insert_match:
                skip_list_insert = int(skip_list_insert_match.group(1))
            skip_list_delete_match = skip_list_delete_re.match(line)
            if skip_list_delete_match:
                skip_list_delete = int(skip_list_delete_match.group(1))
            skip_list_action_match = skip_list_action_re.match(line)
            if skip_list_action_match:
                skip_list_actions = int(skip_list_action_match.group(1))
            tree_set_insert_match = tree_set_insert_re.match(line)
            if tree_set_insert_match:
                tree_set_insert = int(tree_set_insert_match.group(1))
            tree_set_delete_match = tree_set_delete_re.match(line)
            if tree_set_delete_match:
                tree_set_delete = int(tree_set_delete_match.group(1))
            tree_set_action_match = tree_set_action_re.match(line)
            if tree_set_action_match:
                tree_set_actions = int(tree_set_action_match.group(1))

            # When all data for a test size is found, store it in the list
            if all([test_size, skip_list_insert, skip_list_delete, skip_list_actions, tree_set_insert, tree_set_delete, tree_set_actions]):
                data.append({
                    'test_size': test_size,
                    'skip_list_insert': skip_list_insert,
                    'skip_list_delete': skip_list_delete,
                    'skip_list_actions': skip_list_actions,
                    'tree_set_insert': tree_set_insert,
                    'tree_set_delete': tree_set_delete,
                    'tree_set_actions': tree_set_actions
                })
                test_size = None
                skip_list_insert = None
                skip_list_delete = None
                skip_list_actions = None
                tree_set_insert = None
                tree_set_delete = None
                tree_set_actions = None
    return data

# Process all output files
data1 = process_file('output1.txt')
data2 = process_file('output2.txt')
data3 = process_file('output3.txt')

# Merge the data and calculate averages
merged_data = []
for entry1, entry2, entry3 in zip(data1, data2, data3):
    test_size = entry1['test_size']
    merged_data.append({
        'test_size': test_size,
        'skip_list_insert_avg': (entry1['skip_list_insert'] + entry2['skip_list_insert'] + entry3['skip_list_insert']) / 3,
        'skip_list_delete_avg': (entry1['skip_list_delete'] + entry2['skip_list_delete'] + entry3['skip_list_delete']) / 3,
        'skip_list_actions_avg': (entry1['skip_list_actions'] + entry2['skip_list_actions'] + entry3['skip_list_actions']) / 3,
        'tree_set_insert_avg': (entry1['tree_set_insert'] + entry2['tree_set_insert'] + entry3['tree_set_insert']) / 3,
        'tree_set_delete_avg': (entry1['tree_set_delete'] + entry2['tree_set_delete'] + entry3['tree_set_delete']) / 3,
        'tree_set_actions_avg': (entry1['tree_set_actions'] + entry2['tree_set_actions'] + entry3['tree_set_actions']) / 3
    })

# Write the merged data to a CSV file
with open('results.csv', 'w', newline='') as csvfile:
    fieldnames = ['Test Size', 'Skip List Insertion Avg (ms)', 'Tree Set Insertion Avg (ms)', 'Skip List Deletion Avg (ms)', 'Tree Set Deletion Avg (ms)', 'Skip List Actions Avg (ms)', 'Tree Set Actions Avg (ms)']
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

    writer.writeheader()
    for data in merged_data:
        writer.writerow({
            'Test Size': data['test_size'],
            'Skip List Insertion Avg (ms)': data['skip_list_insert_avg'],
            'Tree Set Insertion Avg (ms)': data['tree_set_insert_avg'],
            'Skip List Deletion Avg (ms)': data['skip_list_delete_avg'],
            'Tree Set Deletion Avg (ms)': data['tree_set_delete_avg'],
            'Skip List Actions Avg (ms)': data['skip_list_actions_avg'],
            'Tree Set Actions Avg (ms)': data['tree_set_actions_avg']
        })
